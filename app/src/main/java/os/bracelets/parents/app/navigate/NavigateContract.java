package os.bracelets.parents.app.navigate;

import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/24.
 */

public interface NavigateContract {

    interface View extends BaseView<Presenter> {

    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }
    }
}
