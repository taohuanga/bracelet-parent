package os.bracelets.parents.app.news;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.bean.HealthInfo;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/2/21.
 */

public class HealthInfoPresenter extends HealthInfoContract.Presenter {

    public HealthInfoPresenter(HealthInfoContract.View mView) {
        super(mView);
    }

    @Override
    void informationList(int type, int pageNo, final String releaseTime) {
        ApiRequest.informationList(0, pageNo, releaseTime, new HttpSubscriber() {


            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.loadInfoError();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        JSONArray array = object.optJSONArray("list");
                        if (array != null) {
                            List<HealthInfo> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.optJSONObject(i);
                                HealthInfo info = HealthInfo.parseBean(obj);
                                list.add(info);
                            }
                            if (mView != null)
                                mView.loadInfoSuccess(list);
                        } else {
                            if (mView != null)
                                mView.loadInfoError();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mView != null)
                        mView.loadInfoError();
                }
            }
        });
    }
}
