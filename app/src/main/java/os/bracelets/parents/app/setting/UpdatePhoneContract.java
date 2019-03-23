package os.bracelets.parents.app.setting;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/20.
 */

public interface UpdatePhoneContract {

    interface View extends BaseView<Presenter> {
        void securityCodeSuccess();
    }


    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }

        //获取手机验证码
        abstract void securityCode(int type, String phone);
    }
}
