package os.bracelets.parents.app.personal;

import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/2/23.
 */

public interface PersonalMsgContract {

    interface View extends BaseView<Presenter> {

        void loadInfoSuccess(UserInfo info);

        void uploadImageSuccess(String imageUrl);

        void updateMsgSuccess();
    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }

        abstract void userInfo();

        abstract void uploadImage(String imagePath);

        abstract void updateMsg(String profile, String nickName, String realName, int sex, String birthday,
                                String height, String weight, String location, String longitude, String latitude);
    }

}
