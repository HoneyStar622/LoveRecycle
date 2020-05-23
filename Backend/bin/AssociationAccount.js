var path = require('path');

var app = require(path.resolve(__dirname, '../server/server'));
var ds = app.datasources.db1;

ds.automigrate('AssociationAccount', function (err) {
    if (err) throw err;

    var accounts = [
        {
            accountId: 1,
            associationId: 1
        }
    ];
    var count = accounts.length;
    accounts.forEach(function (account) {
        app.models.AssociationAccount.create(account, function (err, model) {
            if (err) throw err;
            console.log('Created:', model);
            count--;
            if (count === 0)
                ds.disconnect();
        });
    });
});