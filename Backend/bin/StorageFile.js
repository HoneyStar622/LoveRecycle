var path = require('path');

var app = require(path.resolve(__dirname, '../server/server'));
var ds = app.datasources.db1;

ds.automigrate('StorageFile', function (err) {
    if (err) throw err;

    var accounts = [
        {
            "type": "string",
            "name": "string",
            "url": "string"
        }
    ];
    var count = accounts.length;
    accounts.forEach(function (account) {
        app.models.StorageFile.create(account, function (err, model) {
            if (err) throw err;

            console.log('Created:', model);

            count--;
            if (count === 0)
                ds.disconnect();
        });
    });
});
