package os.bracelets.parents.app.news;

import java.util.List;

import os.bracelets.parents.bean.HealthInfo;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/21.
 */

public interface HealthInfoContract {

    interface View extends BaseView<Presenter> {
        void loadInfoSuccess(List<HealthInfo> infoList);

        void loadInfoError();

    }

    abstract class Presenter extends BasePresenter<View> {
        public Presenter(View mView) {
            super(mView);
        }

        abstract void informationList(int type, int pageNo,String releaseTime);
    }

}
