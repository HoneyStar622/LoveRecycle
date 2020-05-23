var path = require('path');

var app = require(path.resolve(__dirname, '../server/server'));
var ds = app.datasources.db1;

ds.automigrate('Association', function (err) {
    if (err) throw err;

    var accounts = [
        {
            name: '社团测试',
            info: '1111',
            accountId: 1,
            icon: '/api/containers/common/download/association_default1586057255976.png'

        }
    ];
    var count = accounts.length;
    accounts.forEach(function (account) {
        app.models.Association.create(account, function (err, model) {
            if (err) throw err;
            console.log('Created:', model);
            count--;
            if (count === 0)
                ds.disconnect();
        });
    });
});
