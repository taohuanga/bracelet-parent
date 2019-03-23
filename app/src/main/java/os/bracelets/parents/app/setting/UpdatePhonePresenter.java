package os.bracelets.parents.app.setting;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/2/20.
 */

public class UpdatePhonePresenter extends UpdatePhoneContract.Presenter {

    public UpdatePhonePresenter(UpdatePhoneContract.View mView) {
        super(mView);
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
    void updatePhone(String oldPhone, String code, String pwd, final String newPhone) {
        ApiRequest.updatePhone(oldPhone, code, pwd, newPhone, new HttpSubscriber() {
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
                    ToastUtil.showShort(result.errorMessage);
                    if (mView != null)
                        mView.updatePhoneSuccess(newPhone);
                }
            }
        });
    }
}
