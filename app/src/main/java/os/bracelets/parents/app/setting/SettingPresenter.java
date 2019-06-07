package os.bracelets.parents.app.setting;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/2/18.
 */

public class SettingPresenter extends SettingContract.Presenter {

    public SettingPresenter(SettingContract.View mView) {
        super(mView);
    }

    @Override
    void loadBaseInfo() {
        ApiRequest.userInfo(new HttpSubscriber() {

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
                        UserInfo info = UserInfo.parseBean(object);
                        if (mView != null)
                            mView.loadInfoSuccess(info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    @Override
//    void walletInfo() {
//        ApiRequest.walletInfo(new HttpSubscriber() {
//            @Override
//            public void onNext(HttpResult result) {
//                super.onNext(result);
//                if(result.code.equals(AppConfig.SUCCESS)){
//                    try {
//                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
//                        WalletInfo info = WalletInfo.parseBean(object);
//                        if(mView!=null)
//                            mView.loadWalletInfoSuccess(info);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
}
