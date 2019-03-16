package os.bracelets.parents.app.nearby;

import java.util.List;

import os.bracelets.parents.bean.NearbyPerson;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/24.
 */

public interface NearbyContract {

    interface View extends BaseView<Presenter>{

        void loadPersonSuccess(List<NearbyPerson> list);


        void loadPersonError();


    }

    abstract class Presenter extends BasePresenter<View>{

        public Presenter(View mView) {
            super(mView);
        }

        abstract void nearbyList(int pageNo);
    }

}
