var path = require('path');

var app = require(path.resolve(__dirname, '../server/server'));
var ds = app.datasources.db1;

ds.automigrate('Activity', function (err) {
    if (err) throw err;

    var accounts = [
        {
            name: '活动测试',
            info: '这是一个测试',
            state: '已结束',
            startDate: '2018-8-20',
            endDate: '2018-10-2',
            associationId: 1,
            icon:'/api/containers/common/download/activity_default1586056678282.png'
        },
        {
            name: '活动测试',
            info: '这是一个这是一个长测试，这是一个长测试，这是一个长测试     ，这是一个长测试测试',
            state: '已结束',
            associationId: 1,
            startDate: '2018-8-20',
            endDate: '2018-10-2',
            icon: '/api/containers/common/download/activity_default1586056678282.png'
        }
    ];
    var count = accounts.length;
    accounts.forEach(function (account) {
        app.models.Activity.create(account, function (err, model) {
            if (err) throw err;
            console.log('Created:', model);
            count--;
            if (count === 0)
                ds.disconnect();
        });
    });
});
