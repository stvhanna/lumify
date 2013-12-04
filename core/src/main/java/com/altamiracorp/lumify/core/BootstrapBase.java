package com.altamiracorp.lumify.core;

import com.altamiracorp.bigtable.model.ModelSession;
import com.altamiracorp.lumify.core.config.Configuration;
import com.altamiracorp.lumify.core.contentTypeExtraction.ContentTypeExtractor;
import com.altamiracorp.lumify.core.fs.FileSystemSession;
import com.altamiracorp.lumify.core.model.GraphSession;
import com.altamiracorp.lumify.core.model.search.SearchProvider;
import com.altamiracorp.lumify.core.model.workQueue.WorkQueueRepository;
import com.altamiracorp.lumify.core.user.SystemUser;
import com.altamiracorp.lumify.core.user.User;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BootstrapBase extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapBase.class);

    private final Configuration config;

    protected BootstrapBase(Configuration config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        LOGGER.info("Creating common bindings");
        User user = new SystemUser();

        bind(ModelSession.class).toInstance(createModelSession());
        bind(FileSystemSession.class).toInstance(createFileSystemSession());
        bind(GraphSession.class).toInstance(createGraphSession());
        bind(SearchProvider.class).toInstance(createSearchProvider(user));
        bind(WorkQueueRepository.class).toInstance(createWorkQueueRepository());
        bind(ContentTypeExtractor.class).toInstance(createContentTypeExtractor());
    }

    private ContentTypeExtractor createContentTypeExtractor() {
        Class contentTypeExtractorClass = null;
        try {
            contentTypeExtractorClass = config.getClass(Configuration.CONTENT_TYPE_EXTRACTOR, null);
            checkNotNull(contentTypeExtractorClass, Configuration.CONTENT_TYPE_EXTRACTOR + " must be configured");
            Constructor<ContentTypeExtractor> contentTypeExtractorConstructor = contentTypeExtractorClass.getConstructor();
            ContentTypeExtractor contentTypeExtractor = contentTypeExtractorConstructor.newInstance();
            contentTypeExtractor.init(config.toMap());
            return contentTypeExtractor;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided model provider " + contentTypeExtractorClass.getName() + " does not have the required constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WorkQueueRepository createWorkQueueRepository() {
        Class workQueueRepositoryClass = null;
        try {
            workQueueRepositoryClass = config.getClass(Configuration.WORK_QUEUE_REPOSITORY, null);
            checkNotNull(workQueueRepositoryClass, Configuration.WORK_QUEUE_REPOSITORY + " must be configured");
            Constructor<WorkQueueRepository> workQueueRepositoryConstructor = workQueueRepositoryClass.getConstructor();
            WorkQueueRepository workQueueRepository = workQueueRepositoryConstructor.newInstance();
            workQueueRepository.init(config.toMap());
            return workQueueRepository;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided model provider " + workQueueRepositoryClass.getName() + " does not have the required constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ModelSession createModelSession() {
        Class modelProviderClass = null;
        try {
            modelProviderClass = config.getClass(Configuration.MODEL_PROVIDER, null);
            checkNotNull(modelProviderClass, Configuration.MODEL_PROVIDER + " must be configured");
            Constructor<ModelSession> modelSessionConstructor = modelProviderClass.getConstructor();
            ModelSession modelSession = modelSessionConstructor.newInstance();
            modelSession.init(config.toMap());
            return modelSession;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided model provider " + modelProviderClass.getName() + " does not have the required constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FileSystemSession createFileSystemSession() {
        Class fileSystemProviderClass = null;
        try {
            fileSystemProviderClass = config.getClass(Configuration.FILESYSTEM_PROVIDER, null);
            checkNotNull(fileSystemProviderClass, Configuration.FILESYSTEM_PROVIDER + " must be configured");
            Constructor<FileSystemSession> modelSessionConstructor = fileSystemProviderClass.getConstructor(Configuration.class);
            return modelSessionConstructor.newInstance(config);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided filesystem provider " + fileSystemProviderClass.getName() + " does not have the required constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private GraphSession createGraphSession() {
        Class graphSessionClass = null;
        try {
            graphSessionClass = config.getClass(Configuration.GRAPH_PROVIDER, null);
            checkNotNull(graphSessionClass, Configuration.GRAPH_PROVIDER + " must be configured");
            Constructor<GraphSession> graphSessionConstructor = graphSessionClass.getConstructor(Configuration.class);
            return graphSessionConstructor.newInstance(config);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided graph provider " + graphSessionClass.getName() + " does not have the required constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SearchProvider createSearchProvider(User user) {
        Class searchProviderClass = null;
        try {
            searchProviderClass = config.getClass(Configuration.SEARCH_PROVIDER, null);
            checkNotNull(searchProviderClass, Configuration.SEARCH_PROVIDER + " must be configured");
            Constructor<SearchProvider> searchProviderConstructor = searchProviderClass.getConstructor(Configuration.class, User.class);
            return searchProviderConstructor.newInstance(config, user);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided search provider " + searchProviderClass.getName() + " does not have the required constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
