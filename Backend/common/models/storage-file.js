'use strict';

var CONTAINER_URL = '/api/containers/';

module.exports = function (StorageFile) {
    StorageFile.upload = function (ctx, options, cb) {
        console.info(ctx.req);
        options = options || {};
        // Firstly, you must create folder  /server/storage/common
        ctx.req.params.container = 'common';
        /**
         * ctx.req    express request object
         * ctx.result express response object
         */
        StorageFile.app.models.Container.upload(ctx.req, ctx.result,
            options, function (err, fileObj) {
                if (err) {
                    return cb(null, {
                        code: 200,
                        message: 'fail',
                        error: err.message
                    });
                } else {
                    // The 'file'below should be the same as field name in the form
                    var fileInfoArr = fileObj.files.file;
                    var objs = [];

                    console.info(fileObj.files);
                    fileInfoArr.forEach(function (item) {
                        
                        objs.push({
                            name: item.name,
                            type: item.type,
                            url: CONTAINER_URL + item.container +
                                '/download/' + item.name,
                        });
                    });
                    console.log(objs);
                    StorageFile.create(objs, function (err, instances) {
                        if (err) {
                            return cb(null, {
                                code: 200,
                                message: 'fail',
                                error: err.message
                            });
                        } else {
                            var res = {
                                code: 200,
                                message: 'success',
                                data: instances
                            }
                            cb(null, res);
                        }
                    });
                }
            });
    };

    StorageFile.remoteMethod(
        'upload', {
            http: { verb: 'post' },
            description: 'Upload a file or more files',
            accepts: [
                { arg: 'ctx', type: 'object', http: { source: 'context' } },
                { arg: 'options', type: 'object', http: { source: 'query' } },
            ],
            returns: {
                arg: 'fileObject', type: 'object', root: true,
            }

        }
    );
};
