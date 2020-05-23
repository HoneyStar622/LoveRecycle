var path = require('path');

var app = require(path.resolve(__dirname, '../server/server'));
var ds = app.datasources.db1;

ds.automigrate('Account', function (err) {
    if (err) throw err;

    var accounts = [
        {
            username: 'test',
            email: 'test@qq.com',
            info: '简介',
            password: 'test',
            realm:'测试',
            icon: '/api/containers/common/download/me_icon1586056523192.png',
            studentId: '00000000'
        },

        {
            username: 'wanggh8',
            email: '1299927852@qq.com',
            info: '简介',
            password: 'wanggh8',
            realm:'王广浩',
            icon: '/api/containers/common/download/me_icon1586056523192.png',
            studentId: '16340211'
        },
        {
            username: 'wangjn',
            email: '850537362@qq.com',
            password: '12345',
            info: '简介',
            realm:'王季宁',
            icon: '/api/containers/common/download/me_icon1586056523192.png',
            studentId: '16340212'
        },
        {
            username: 'minister',
            email: 'minister@qq.com',
            info: '简介',
            password: 'minister',
            realm:'生活部部长',
            icon: '/api/containers/common/download/me_icon1586056523192.png',
            studentId: '16340213'
        }

    ];
    var count = accounts.length;
    accounts.forEach(function (account) {
        app.models.Account.create(account, function (err, model) {
            if (err) throw err;
            console.log('Created:', model);
            count--;
            if (count === 0)
                ds.disconnect();
        });
    });
});
