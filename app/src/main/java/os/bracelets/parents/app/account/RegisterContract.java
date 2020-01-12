package os.bracelets.parents.app.account;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/20.
 */

public interface RegisterContract {


    interface View extends BaseView<Presenter> {

        void codeSuccess();

        void registerSuccess(String phone);
    }

    abstract class Presenter extends BasePresenter<View> {
        public Presenter(View mView) {
            super(mView);
        }

        abstract void code(int type, String phone,String areaCode);

        abstract void register(String phone,String securityCode,String code,String password);
    }

}
