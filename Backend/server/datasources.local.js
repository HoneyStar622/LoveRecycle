'use strict';

var path = require('path');

module.exports = {
  picture: {
    
    // 限定上传文件大小为50M
    maxFileSize: 100 * 1024 * 1024,

    // 自定义文件名
    getFilename: function(fileInfo) {
      var fileName = fileInfo.name.replace(/\s+/g, '-').toLowerCase();
      var fileObj = path.parse(fileName);
      // 给文件名加上时间戳
      return fileObj.name + Date.now() + fileObj.ext;
    },
  },
};
