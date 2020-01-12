package os.bracelets.parents.app.setting;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/2/20.
 */

public class UpdatePwdPresenter extends UpdatePwdContract.Presenter {

    public UpdatePwdPresenter(UpdatePwdContract.View mView) {
        super(mView);
    }

    @Override
    void code(int type, String phone) {
        ApiRequest.code(type, phone, "", new HttpSubscriber() {

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
                        mView.codeSuccess();
                }
            }
        });
    }


    @Override
    void updatePwd(String oldPwd, String password, String securityCode) {
        ApiRequest.updatePwd(oldPwd, password, securityCode, new HttpSubscriber() {
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
                        mView.resetPwdSuccess();
                }
            }
        });
    }
}
