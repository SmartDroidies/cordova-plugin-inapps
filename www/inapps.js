"use strict";

var exec = require("cordova/exec");

var InApps = {

    initialize : function (topic, success, error) {
        exec(success, error, "SmartInApps", "initialize", []);
    }

    /*,

    unsubscribe: function (topic, success, error) {
        exec(success, error, "SmartFirebase", "unsubscribe", [topic]);
    },

    event: function(key, value, success, error) {
    	exec(success, error, "SmartFirebase", "event", [key, value]);
	},

    exception: function(message, success, error) {
        exec(success, error, "SmartFirebase", "exception", [message]);
    },

    getInstanceId: function(success, error) {
        exec(success, error, "SmartFirebase", "getInstanceId", []);
    }*/


};

module.exports = InApps;