cordova.define("com.lakomov.MediaLibrary.MediaLibrary", function(require, exports, module) {
    //
    // Use jsdoc to generate documentation.

    // The following line causes a jsdoc error.
    // Use the jsdoc option -l to ignore the error.
    var exec = cordova.require('cordova/exec');


    exports.showMediaPicker = function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, "MediaLibrary", "showMediaPicker", [params]);
    };

});
