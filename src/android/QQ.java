package com.cooljeffrey;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeffrey on 4/30/15.
 */
public class QQ extends CordovaPlugin {
    public static final String APPID_PROPERTY_KEY = "QQAPPID";

    private static final int QQ_NOT_INSTALLED = 1;
    private static final int QQ_REQUEST_CANCELLED = 2;
    private static final int QQ_INVALID_REQUEST_JSON = 3;
    private static final int QQ_UNKNOWN_ERROR = 99;

    private String APP_ID;
    private Tencent mTencent;

    private class BaseUiListener implements IUiListener {
        private CallbackContext context;
        public BaseUiListener(CallbackContext context){
            this.context = context;
        }
        @Override
        public void onComplete(Object response) {
            System.out.println(response.toString());
            if(response instanceof JSONObject){
                JSONObject resp = (JSONObject) response;
                this.context.success(resp);
            }else{
                this.context.success(response.toString());
            }
        }

        @Override
        public void onError(UiError e) {
            System.out.println(e.toString());
            this.context.error(new ErrorMessage(e.errorCode, e.errorMessage, e.errorDetail));
        }
        @Override
        public void onCancel() {
            System.out.println("Cancelled.");
            this.context.error(QQ_REQUEST_CANCELLED);
        }
    }

    class ErrorMessage extends JSONObject {
        public ErrorMessage(int code, String message, String details) {
            try {
                this.put("code", code);
                this.put("message", message);
                this.put("details", details);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        public ErrorMessage(int code, String message) {
            try {
                this.put("code", code);
                this.put("message", message);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void pluginInitialize(){
        this.APP_ID = webView.getProperty(APPID_PROPERTY_KEY, "");
        mTencent = Tencent.createInstance(this.APP_ID, this.cordova.getActivity().getApplicationContext());
    }

    @Override
    public boolean execute(String action, final JSONArray args,
                           final CallbackContext context) throws JSONException {
        boolean result = false;
        try {
            if (action.equals("share")) {
                final QQ me = this;
                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject cfg = args.getJSONObject(0);
                            me.share(cfg, context);
                        } catch (JSONException e) {
                            context.error("JSON Exception");
                            e.printStackTrace();
                        }
                    }
                });
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.error(new ErrorMessage(10000,e.getMessage()));
            result = false;
        }
        return result;
    }

    public void share(JSONObject json, CallbackContext context){
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, json.getInt(QQShare.SHARE_TO_QQ_KEY_TYPE));
            if(json.has(QQShare.SHARE_TO_QQ_TARGET_URL) && !json.isNull(QQShare.SHARE_TO_QQ_TARGET_URL)) {
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, json.getString(QQShare.SHARE_TO_QQ_TARGET_URL));
            }
            if(json.has(QQShare.SHARE_TO_QQ_TITLE) && !json.isNull(QQShare.SHARE_TO_QQ_TITLE)) {
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, json.getString(QQShare.SHARE_TO_QQ_TITLE));
            }
            if(json.has(QQShare.SHARE_TO_QQ_IMAGE_URL) && !json.isNull(QQShare.SHARE_TO_QQ_IMAGE_URL)) {
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, json.getString(QQShare.SHARE_TO_QQ_IMAGE_URL));
            }
            if(json.has(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL) && !json.isNull(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL)) {
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, json.getString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL));
            }
            if(json.has(QQShare.SHARE_TO_QQ_SUMMARY) && !json.isNull(QQShare.SHARE_TO_QQ_SUMMARY)) {
                bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, json.getString(QQShare.SHARE_TO_QQ_SUMMARY));
            }
            if(json.has(QQShare.SHARE_TO_QQ_APP_NAME) && !json.isNull(QQShare.SHARE_TO_QQ_APP_NAME)) {
                bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, json.getString(QQShare.SHARE_TO_QQ_APP_NAME));
            }
            if(json.has(QQShare.SHARE_TO_QQ_APP_NAME) && !json.isNull(QQShare.SHARE_TO_QQ_APP_NAME)) {
                bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, json.getString(QQShare.SHARE_TO_QQ_APP_NAME) + APP_ID);
            }
            mTencent.shareToQQ(this.cordova.getActivity(), bundle, new BaseUiListener(context));
        }catch(JSONException ex){
            ex.printStackTrace();
            context.error(new ErrorMessage(QQ_INVALID_REQUEST_JSON, ex.getMessage()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }
}
