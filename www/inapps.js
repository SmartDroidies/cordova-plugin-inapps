"use strict";

var exec = require("cordova/exec");

var InApps = {

    initialize : function (success, error) {
        exec(success, error, "SmartInApps", "initialize", []);
    },

    subscribe : function (productid, success, error) {
        exec(success, error, "SmartInApps", "subscribe", [productid]);
    },

    isSubscribed : function (productid, success, error) {
        exec(success, error, "SmartInApps", "isSubscribed", [productid]);
    },

    query : function (productid, success, error) {
        exec(success, error, "SmartInApps", "query", [productid]);  
    }

};

module.exports = InApps;