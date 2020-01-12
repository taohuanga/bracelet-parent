package os.bracelets.parents.app.account;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/21.
 */

public interface ResetPwdContract {

    interface View extends BaseView<Presenter> {
        void codeSuccess();

        void resetPwdSuccess();
    }


    abstract class Presenter extends BasePresenter<View> {
        public Presenter(View mView) {
            super(mView);
        }

        abstract void code(int type, String phone,String areaCode);

        abstract void resetPwd(String phone,String pwd,String securityCode);
    }

}
