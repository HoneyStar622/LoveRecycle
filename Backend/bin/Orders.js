var path = require('path');

var app = require(path.resolve(__dirname, '../server/server'));
var ds = app.datasources.db1;

ds.automigrate('Orders', function (err) {
    if (err) throw err;

    var goods = [
        {
            category: "book",
            info: '计算机网络',
            picture: '',
            state: '',
            repay: '2',
            date: '2018-09-20',
            place: '东校区至善园3号',
            accountId: 2,
            activityId: 1
        }
    ];
    var count = goods.length;
    goods.forEach(function (account) {
        app.models.Orders.create(account, function (err, model) {
            if (err) throw err;
            console.log('Created:', model);
            count--;
            if (count === 0)
                ds.disconnect();
        });
    });
});
