package os.bracelets.parents.app.news;

import os.bracelets.parents.bean.InfoDetail;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/3/21.
 */

public interface InfoDetailContract {

    interface View extends BaseView<Presenter> {
        void  loadSuccess(InfoDetail detail);
    }

    abstract class Presenter extends BasePresenter<View> {
        public Presenter(View mView) {
            super(mView);
        }

        abstract void loadInfoDetail(String infoId);
    }

}
