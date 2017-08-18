package com.smart.droid.inapps;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SmartInApps extends CordovaPlugin {

    private final String TAG = "SmartInApps";


    @Override
    protected void pluginInitialize() {
        final Context context = this.cordova.getActivity().getApplicationContext();
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Log.d(TAG, "Starting SmartInApps plugin");
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getInstanceId")) {
            this.getInstanceId(callbackContext);
            return true;
        } else if (action.equals("subscribe")) {
            this.subscribe(callbackContext, args.getString(0));
            return true;
        } else if (action.equals("unsubscribe")) {
            this.unsubscribe(callbackContext, args.getString(0));
            return true;
        } else if (action.equals("onNotificationOpen")) {
            //this.registerOnNotificationOpen(callbackContext);
            return true;
        } else if (action.equals("event")) {
            this.trackEvent(callbackContext, args.getString(0), args.getString(1));
            return true;
        } else if (action.equals("exception")) {
            this.trackException(callbackContext, args.getString(0));
            return true;
        }
        return false;
    }


    private void trackEvent(final CallbackContext callbackContext, final String key, final String value) {
        final Bundle params = new Bundle();
        params.putString(key, value);
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    //mFirebaseAnalytics.logEvent(key, params);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void trackException(final CallbackContext callbackContext, final String msg) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    //FirebaseCrash.report(new Exception(msg));
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void subscribe(final CallbackContext callbackContext, final String topic) {
        Log.d(TAG, "Subscribe for topic : " + topic);
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    //FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void unsubscribe(final CallbackContext callbackContext, final String topic) {
        Log.d(TAG, "UnSubscribe for topic : " + topic);
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    //FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }


    private void getInstanceId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    //String token = FirebaseInstanceId.getInstance().getToken();
                    String token = null;
                    callbackContext.success(token);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "Broadcast Message Recieved. Trigger Java Script");
            sendPushToJavascript(intent.getStringExtra("data"));
        }
    };

    private void sendPushToJavascript(String data) {
        //Log.d(TAG, "sendPushToJavascript: " + data);

        if (data != null) {
            //We remove the last saved push since we're sending one.
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(cordova.getActivity());
            //sharedPreferences.edit().remove(LAST_PUSH_KEY).apply();

            final String js = "javascript:onNotification(" + JSONObject.quote(data).toString() + ")";
            webView.getEngine().loadUrl(js, false);
        }
    }


    @Override
    public Object onMessage(String id, Object data) {
        if (id.equals("onPageFinished")) {
            //This here is to catch throw the notification once the app has been down.
            //TODO: Maybe there is a better place to do this ? or another way to do this ?
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(cordova.getActivity());

            //String lastPush = sharedPreferences.getString(LAST_PUSH_KEY, null);
            //if (lastPush != null) {
              //  sendPushToJavascript(lastPush);
            //}
        }
        return super.onMessage(id, data);
    }

    private void unregisterBroadcastReceivers() {
        if (mMessageReceiver != null) {
            //LocalBroadcastManager.getInstance(cordova.getActivity()).unregisterReceiver(mMessageReceiver);
        }
    }

    @Override
    public void onDestroy() {
        unregisterBroadcastReceivers();
        super.onDestroy();
    }


}