package os.bracelets.parents.app.account;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/1/24.
 */

public class LoginPresenter extends LoginContract.Presenter {


    public LoginPresenter(LoginContract.View mView) {
        super(mView);
    }

    @Override
    void login(String name, String password) {
        ApiRequest.login(name, password, new HttpSubscriber() {

            @Override
            public void onStart() {
                super.onStart();
                if (mView != null)
                    mView.showLoading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (mView != null)
                    mView.hideLoading();
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        BaseInfo info = BaseInfo.parseBean(object);
                        SPUtils.put(MyApplication.getInstance(), AppConfig.TOKEN_ID, info.getTokenId());
                        SPUtils.put(MyApplication.getInstance(), AppConfig.USER_ID, info.getUserId() + "");
                        SPUtils.put(MyApplication.getInstance(), AppConfig.USER_IMG, info.getIcon() + "");
                        SPUtils.put(MyApplication.getInstance(), AppConfig.USER_NICK, info.getNickName() + "");
                        if(mView!=null)
                            mView.loginSuccess(info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    void securityCode(int type, String phone) {
        ApiRequest.code(type, phone, new HttpSubscriber() {

            @Override
            public void onStart() {
                super.onStart();
                if (mView != null)
                    mView.showLoading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (mView != null)
                    mView.hideLoading();
                if (result.code.equals(AppConfig.SUCCESS)) {
                    if (mView != null)
                        mView.securityCodeSuccess();
                }
            }
        });
    }

    @Override
    void fastLogin(String phone, String securityCode) {
        ApiRequest.fastLogin(phone, securityCode, new HttpSubscriber() {

            @Override
            public void onStart() {
                super.onStart();
                if (mView != null)
                    mView.showLoading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.hideLoading();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (mView != null)
                    mView.hideLoading();
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        BaseInfo info = BaseInfo.parseBean(object);
                        SPUtils.put(MyApplication.getInstance(), AppConfig.TOKEN_ID, info.getTokenId());
                        SPUtils.put(MyApplication.getInstance(), AppConfig.USER_ID, info.getUserId() + "");
                        SPUtils.put(MyApplication.getInstance(), AppConfig.USER_IMG, info.getIcon() + "");
                        SPUtils.put(MyApplication.getInstance(), AppConfig.USER_NICK, info.getNickName() + "");
                        if(mView!=null)
                            mView.loginSuccess(info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
