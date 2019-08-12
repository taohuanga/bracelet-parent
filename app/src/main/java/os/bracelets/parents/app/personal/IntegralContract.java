package os.bracelets.parents.app.personal;

import java.util.List;

import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

public interface IntegralContract {

    interface View extends BaseView<Presenter> {
        void integralSuccess(List<IntegralInfo> list);

        void integralError();


        void loadWalletInfoSuccess(WalletInfo info);
    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }

        abstract void integralSerialList(int type,int pageIndex,String startTime,String endTime);

        abstract void  walletInfo();

    }

}
