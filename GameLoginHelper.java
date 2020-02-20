package com.ushareit.logindialog.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ushareit.common.appertizers.Logger;
import com.ushareit.common.lang.ObjectStore;
import com.ushareit.common.utils.TaskHelper;
import com.ushareit.common.widget.SafeToast;
import com.ushareit.logindialog.dialogfragment.GameLoginDialogFragment;
import com.ushareit.logindialog.httpinterface.GameHttpHelp;
import com.ushareit.logindialog.model.GameLoginModel;
import com.ushareit.logindialog.model.GameTokenModel;
import com.ushareit.net.NetUtils;
import com.ushareit.upload.BeylaEventUtil;
import com.ushareit.upload.FlurryEventUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * created by anjingshuai
 * on 2019-08-02
 */
public class GameLoginHelper {

    private static final String TAG = "GameLoginHelper";

    public static final String LOGIN_ACTION = "login";
    public static final String REGISTER_ACTION = "register";

    private OnLoginCompleteListener mOnLoginCompleteListener;
    private boolean mLoginLoaded;//login dialog is loaded
    private volatile static GameLoginHelper sInstance;

    private Context mContext;
    private String mToken;

    private GameLoginHelper() {
        if (mContext == null)
            mContext = ObjectStore.getContext();
    }

    public static GameLoginHelper getInstance() {
        if (sInstance == null) {
            synchronized (GameLoginHelper.class) {
                if (sInstance == null) {
                    sInstance = new GameLoginHelper();
                }
            }
        }
        return sInstance;
    }

    public void userLogin(Context context, String mClientId, String mClientSecret, OnLoginCompleteListener listener) {
        this.mContext = context;
        setOnLoginCompleteListener(listener);
        mToken = GameSettings.getUserToken();
        //Judge if the saved mToken is null
        if (!TextUtils.isEmpty(mToken)) {
            showLoginDialog();
            return;
        }

        //judge if mClientId and mClientSecret delivered by developer is null
        if (TextUtils.isEmpty(mClientId) || TextUtils.isEmpty(mClientSecret)) {
            Logger.e(TAG, "clientId or clientSecret is empty can cause NullPointerException");
            throw new NullPointerException("Neither the clientId nor clientSecret can be empty");
        }
        if (isNetworkConnected())
            requestGetToken(mClientId, mClientSecret);
        else
            Logger.e(TAG, "no network connected, please check the network");

    }

    private void requestGetToken(final String mClientId, final String mClientSecret) {
        TaskHelper.execZForSDK(new TaskHelper.Task() {
            GameTokenModel models = null;

            @Override
            public void execute() throws Exception {
                models = GameHttpHelp.postGetGameToken(mClientId, mClientSecret);
            }

            @Override
            public void callback(Exception e) {
                if (e != null) {
                    Logger.d(TAG, "getGameToken failed：" + e.getMessage());
                    SafeToast.showToast("The gameSecret and appId are not corresponding!", Toast.LENGTH_LONG);
                    return;
                }
                if (models != null && models.getData() != null) {
                    mToken = models.getData().getAccessToken();
                    int expire = models.getData().getExpiresIn();
                    //save user token and current time
                    GameSettings.saveUserTokenAndTime(mToken, expire);
                    Logger.d(TAG, "getGameToken() success，token is" + mToken);

                    showLoginDialog();
                }
            }
        });
    }

    private void showLoginDialog() {
        if (isLogin()) {
            setReturnLoginData();
            return;
        }
        openLoginDialog();
    }

    public String getUserId() {
        String userId = GameSettings.getUserId();
        if (TextUtils.isEmpty(userId)) {
            userId = "";
        }
        return userId;
    }

    public void saveUserModelInfo(GameLoginModel models) {
        if (models.getData() == null)
            return;

        Gson gson = new Gson();
        String userModel = gson.toJson(models.getData());
        GameSettings.saveUserModel(userModel);
        GameSettings.saveUserId(models.getData().getUserId());
    }

    public void setReturnLoginData() {
        String userModelString = GameSettings.getUserModel();
        if (TextUtils.isEmpty(userModelString))
            return;
        if (mOnLoginCompleteListener == null)
            return;

        try {
            GameLoginModel.DataBean dataBean = new GameLoginModel.DataBean();
            dataBean.parseData(userModelString);
            if (dataBean != null)
                mOnLoginCompleteListener.onLoginSuccess(dataBean.getUserId(),
                        dataBean.getUsername(), dataBean.getAvatarUrl());
        } catch (Exception e) {
            mOnLoginCompleteListener.onLoginSuccess("", "", "");
        }
    }

    public boolean logout() {
        try {
            GameSettings.saveUserModel("");
            GameSettings.saveUserId("");
            GameSettings.saveUserTokenAndTime("", 0);
            Logger.e(TAG, "logout success!!!");
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "logout fail:)" + e.getMessage());
            return false;
        }
    }

    //is login status
    public boolean isLogin() {
        String userModelString = GameSettings.getUserModel();
        if (!TextUtils.isEmpty(userModelString) && !TextUtils.isEmpty(getUserId())) {
            return true;
        }
        return false;
    }

    private void openLoginDialog() {
        if (mLoginLoaded)
            return;

        BeylaEventUtil.uploadShowLoginUiBeylaEvent();
        mLoginLoaded = true;

        //init login dialog
        GameLoginDialogFragment gameLoginFragment = GameLoginDialogFragment.getInstance();
        try {
            gameLoginFragment.show(((Activity) mContext).getFragmentManager(), "GameLoginDialogFragment");
        } catch (Exception e) {
            Logger.d(TAG, "show login dialog error,context is " + mContext);
            throw new RuntimeException("Context param must be an instance of Activity and cannot be null");
        }
    }

    @Deprecated
    public void gameStart() {
    }

    @Deprecated
    public void gameEnd() {
    }

    @Deprecated
    public void gameLevelStart(String level) {
        BeylaEventUtil.gameLevelStart(level);
    }

    @Deprecated
    public void gameLevelEnd(String level) {
        BeylaEventUtil.gameLevelEnd(level);
    }

    public void flurryEvent(String eventId) {
        FlurryEventUtil.onEventNoParam(eventId);
    }

    public void flurryEventWithParam(String eventId, HashMap map) {
        FlurryEventUtil.onEventWithParam(eventId, map);
    }

    public void flurryEventWithTime(String eventId, HashMap map) {
        FlurryEventUtil.onEventWithTime(eventId, map);
    }

    public void flurryEventEndTime(String eventId) {
        FlurryEventUtil.onEventEndTime(eventId);
    }

    public void flurryEventWithPayment( String productName,
                                        String productId,
                                        int quantity,
                                        double price,
                                        String currency,
                                        String transactionId,
                                        Map<String, String> parameters) {
        FlurryEventUtil.onEventPayment(productName, productId,
                quantity, price, currency, transactionId, parameters);
    }

    public void setOnLoginCompleteListener(OnLoginCompleteListener listener) {
        this.mOnLoginCompleteListener = listener;
    }

    public void resetLoginFlag() {
        mLoginLoaded = false;
    }

    public boolean isNetworkConnected() {
        Pair<Boolean, Boolean> netInfo = NetUtils.checkConnected(ObjectStore.getContext());
        if (!netInfo.first && !netInfo.second) {
            SafeToast.showToast("Please check the network connect!", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    public interface OnLoginCompleteListener {
        void onLoginSuccess(String userId, String username, String avatarUrl);
    }
}
