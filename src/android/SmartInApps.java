package com.smart.droid.inapps;

import android.content.Context;
import android.util.Log;

import com.smart.droid.inapps.util.IabHelper;
import com.smart.droid.inapps.util.IabResult;
import com.smart.droid.inapps.util.Inventory;
import com.smart.droid.inapps.util.Purchase;
import com.smart.droid.inapps.util.SkuDetails;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SmartInApps extends CordovaPlugin {

    private final String TAG = "SmartInApps";
    private IabHelper mHelper;

    @Override
    protected void pluginInitialize() {
        final Context context = this.cordova.getActivity().getApplicationContext();
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Log.d(TAG, "Starting SmartInApps plugin");
                String base64EncodedPublicKey;
                base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjHgUd91r2exC5mGBjfaZSReHmd4ppLEvDsu7UBBSkjLa2RuCs7umKrXUxGyAavrD7kUW6xd3NBiq9z9c1Y9WgdogExRRQ5wk5ILsOPxXplJL36pDBdsWsbppWRFaN1pacIJXLL4ICtREOOWnpm1cJZsXNISFOUSCC2PT7nsBUWAiCD4/B4V/bAnrxSweJILOe5i3svvke9u902GkOWUb8b/0mt4r/5YGt1nO11IcNH/X8+GDjkET9GYQ2YcN27CHB8fdgkwKUfX7vOMrGLTjPUQaqR/n8zeWlRr/Pf4fuR82hG2Vec6fH+rOoGZZgZfD7EG24dQQsZBIwfjmacXeqwIDAQAB";
                mHelper = new IabHelper(context, base64EncodedPublicKey);
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    public void onIabSetupFinished(IabResult result) {
                        if (!result.isSuccess()) {
                            // Oh no, there was a problem.
                            Log.d(TAG, "Problem setting up In-app Billing: " + result);
                        } else {
                            //Log.d(TAG, "Hooray, IAB is fully set up!");
                        }
                    }
                });

            }
        });

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("initialize")) {
            this.initialize(callbackContext);
            return true;
        } else if (action.equals("subscribe")) {
            this.subscribe(callbackContext, args.getString(0));
            return true;
        } else if (action.equals("query")) {
            this.query(callbackContext, args.getString(0));
            return true;
        } else if (action.equals("isSubscribed")) {
            this.isSubscribed(callbackContext, args.getString(0));
            return true;
        }
        return false;
    }

    private void initialize(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                //Log.d(TAG, "Initialize Not required!");
            }
        });
    }

    private void subscribe(final CallbackContext callbackContext, final String pid) {
        Log.d(TAG, "Subscribe for InApp products : " + pid);
        //final String pidt = "android.test.purchased";
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    mHelper.launchPurchaseFlow(cordova.getActivity(), pid, 10001,
                            new IabHelper.OnIabPurchaseFinishedListener() {
                                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                                    if (result.isFailure()) {
                                        callbackContext.error(result.getResponse());
                                        //Log.d(TAG, "Error purchasing: " + result);
                                    } else {
                                        //Log.d(TAG, "Success purchasing: " + purchase.getSku());
                                        callbackContext.success();
                                    }
                                }
                            }, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    callbackContext.error("Failed to subscribe ; " + e.getMessage());
                }
            }
        });
    }

    private void query(final CallbackContext callbackContext, final String productid) {
        /*cordova.getThreadPool().execute(new Runnable() {
            public void run() { */
        List<String> productList = new ArrayList<String>();
        productList.add(productid);
        try {
            mHelper.queryInventoryAsync(true, null, productList,
                    new IabHelper.QueryInventoryFinishedListener() {
                        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                            if (result.isFailure()) {
                                // handle error
                                return;
                            }

                            SkuDetails subscription = inventory.getSkuDetails(productid);
                            if (subscription != null) {
                                Log.d(TAG, "Subscription details : " + subscription.getDescription() + ", " + subscription.getTitle());
                            } else {
                                Log.d(TAG, "Subscription is emtpy");
                            }

                        }
                    });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            callbackContext.error("Failed to get subscription details ; " + e.getMessage());
        }
            /*}
        });*/
    }

    private void isSubscribed(final CallbackContext callbackContext, final String productid) {
        List<String> productList = new ArrayList<String>();
        productList.add(productid);
        try {
            mHelper.queryInventoryAsync(true, null, productList,
                    new IabHelper.QueryInventoryFinishedListener() {
                        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                            if (result.isFailure()) {
                                // handle error
                                callbackContext.error("Failed to get subscription details");
                            } else {
                                Log.d(TAG, "Has subscription : " + inventory.hasPurchase(productid));
                                callbackContext.success("" + inventory.hasPurchase(productid));
                            }
                        }
                    });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            callbackContext.error("Exception to get subscription details ; " + e.getMessage());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
            mHelper = null;
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

}