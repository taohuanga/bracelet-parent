package os.bracelets.parents.app.setting;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/20.
 */

public interface UpdatePhoneContract {

    interface View extends BaseView<Presenter> {

    }


    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }
    }
}
