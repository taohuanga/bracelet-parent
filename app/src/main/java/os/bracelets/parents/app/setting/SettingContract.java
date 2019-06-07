package os.bracelets.parents.app.setting;

import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/18.
 */

public interface SettingContract {

    interface View extends BaseView<Presenter> {

        void loadInfoSuccess(UserInfo info);

//        void loadWalletInfoSuccess(WalletInfo info);

    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }

        abstract void loadBaseInfo();

//        abstract void walletInfo();
    }

}
