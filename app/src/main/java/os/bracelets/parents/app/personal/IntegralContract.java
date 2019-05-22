package os.bracelets.parents.app.personal;

import java.util.List;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

public interface IntegralContract {

    interface View extends BaseView<Presenter> {
        void integralSuccess(List<IntegralInfo> list);
    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }

        abstract void integralSerialList();
    }

}
