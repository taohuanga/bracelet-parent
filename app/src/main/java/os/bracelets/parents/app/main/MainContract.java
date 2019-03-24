package os.bracelets.parents.app.main;

import java.util.List;

import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.RemindBean;
import os.bracelets.parents.bean.WeatherInfo;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.BaseView;

/**
 * Created by lishiyou on 2019/1/27.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        //数据保存
        void loginWeatherSuccess(WeatherInfo info);

        void loadMsgSuccess(int stepNum, List<RemindBean> list);

//        void loadRemindSuccess();

        void loadSports(String number);
    }

    abstract class Presenter extends BasePresenter<View> {

        public Presenter(View mView) {
            super(mView);
        }

        abstract void homeMsg();

//        abstract void remindList();

        abstract void dailySports();

        abstract void getWeather();

        abstract void loginHx(BaseInfo info);

    }

}
