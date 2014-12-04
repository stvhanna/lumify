
define([
    '../util/ajax',
    './storeHelper'
], function(ajax, storeHelper) {
    'use strict';

    var api = {

        audit: function(edgeId) {
            return ajax('GET', '/edge/audit', {
                edgeId: edgeId
            });
        },

        create: function(options) {
            return ajax('POST', '/edge/create', options);
        },

        'delete': function(edgeId, sourceId, targetId) {
            return ajax('DELETE', '/edge', {
                edgeId: edgeId,
                sourceId: sourceId,
                targetId: targetId
            });
        },

        properties: function(edgeId) {
            return ajax('GET', '/edge/properties', {
                graphEdgeId: edgeId
            });
        },

        setProperty: function(edgeId, property, optionalWorkspaceId) {
            var url = '/edge/' + (
                property.name === 'http://lumify.io/comment#entry' ?
                'comment' : 'property'
            );

            return ajax('POST', url, _.tap({
                 edgeId: edgeId,
                 propertyName: property.name,
                 value: property.value,
                 visibilitySource: property.visibilitySource,
                 justificationText: property.justificationText
            }, function(params) {
                if (property.sourceInfo) {
                    params.sourceInfo = JSON.stringify(property.sourceInfo);
                }
                if (property.key) {
                    params.propertyKey = property.key;
                }
                if (property.metadata) {
                    params.metadata = JSON.stringify(property.metadata)
                }
                if (optionalWorkspaceId) {
                    params.workspaceId = optionalWorkspaceId;
                }
            }));
        },

        deleteProperty: function(edgeId, property) {
            return ajax('DELETE', '/edge/property', {
                edgeId: edgeId,
                propertyName: property.name,
                propertyKey: property.key
            })
        },

        store: storeHelper.createStoreAccessorOrDownloader(
            'edge', 'edgeId', null,
            function(toRequest) {
                if (toRequest.length > 1) {
                    throw new Error('Can only get one edge at a time');
                }
                return api.properties(toRequest[0]);
            }),

        setVisibility: function(edgeId, visibilitySource) {
            return ajax('POST', '/edge/visibility', {
                graphEdgeId: edgeId,
                visibilitySource: visibilitySource
            });
        },

    };

    return api;
});
