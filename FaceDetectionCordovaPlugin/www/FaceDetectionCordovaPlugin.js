var exec = require('cordova/exec');
var testing = function(){};
testing.prototype.camera = function (arg0, success, error) {
    exec(success, error, 'FaceDetectionCordovaPlugin', 'ProcessAndDetect', [arg0]);
};
testing.install = function () {
    if (!window.plugins) {
      window.plugins = {};
    }
    window.plugins.start = new testing();
    return window.plugins.start;
  
  };
cordova.addConstructor(testing.install);
