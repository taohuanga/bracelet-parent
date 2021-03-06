package os.bracelets.parents.app.setting;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/20.
 */

public interface UpdatePwdContract {

    interface View extends BaseView<Presenter> {

        void codeSuccess();

        void resetPwdSuccess();

    }

    abstract class Presenter extends BasePresenter<View> {
        public Presenter(View mView) {
            super(mView);
        }

        abstract void code(int type, String phone);


        abstract void updatePwd(String oldPwd,String password,String securityCode);
    }

}
