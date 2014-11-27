cordova.define("com.firerunner.cordova.MediaLibrary", function (require, exports, module) { 
    //
    // Use jsdoc to generate documentation.

    // The following line causes a jsdoc error.
    // Use the jsdoc option -l to ignore the error.
    var exec = cordova.require('cordova/exec');


exports.isSupported = function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, "MediaLibrary", "isSupported", [params]);
    };

    exports.initialize = function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, "MediaLibrary", "initialize", [params]);
    };
    exports.showMediaPicker = function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, "MediaLibrary", "showMediaPicker", [params]);
    };
    exports.play = function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, "MediaLibrary", "play", [params]);
    };
    exports.pause = function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, "MediaLibrary", "pause", [params]);
    };
    exports.getArtists = function (successCallback, errorCallback, params) {
            cordova.exec(successCallback, errorCallback, "MediaLibrary", "getArtists", [params]);
        };
        exports.getAlbumsByArtist = function (successCallback, errorCallback, params) {
                    cordova.exec(successCallback, errorCallback, "MediaLibrary", "getAlbumsByArtist", [params]);
                };

                 exports.getSongsByAlbum = function (successCallback, errorCallback, params) {
                            cordova.exec(successCallback, errorCallback, "MediaLibrary", "getSongsByAlbum", [params]);
                        };
                        exports.getAlbums = function (successCallback, errorCallback, params) {
                                                    cordova.exec(successCallback, errorCallback, "MediaLibrary", "getAlbums", [params]);
                                                };
    exports.getPlaylists = function (successCallback, errorCallback, params) {
            cordova.exec(successCallback, errorCallback, "MediaLibrary", "getPlaylists", [params]);
        };

        exports.getSongsByPlaylist = function (successCallback, errorCallback, params) {
                    cordova.exec(successCallback, errorCallback, "MediaLibrary", "getSongsByPlaylist", [params]);
                };
    exports.getSongFiles = function (successCallback, errorCallback, params) {
                cordova.exec(successCallback, errorCallback, "MediaLibrary", "getSongFiles", [params]);
            };
});