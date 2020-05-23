module.exports = function (Account) {
    
    Account.changePd = function (user, cb) {
      Account.findOne({ where: { email: user.email } }, function (err, instance) {
        if (instance == null) {
          var res = {
            code: 200,
            message: 'fail',
            error: 'no account'
          };
          cb(null, res);
        }
        else {
          
          Account.updateAll({ email: user.email }, { password: user.password }, function (err, instance1) {
            var res = {
              code: 200,
              message: 'success',
              data: instance
            };
            cb(null, res);
          });
        }
      });
    };
    
  
    //dataSource.connector.execute（sql_stmt，params，callback）;
    
    Account.remoteMethod('changePd',
      {
        http: { path: '/changePd', verb: 'post' },
        accepts: { arg: 'user', type: 'object', required: true, http: { source: 'body' } },
        returns: { arg: 'response', type: 'object' }
      });

    
  }
  