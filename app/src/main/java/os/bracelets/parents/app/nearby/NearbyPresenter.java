package os.bracelets.parents.app.nearby;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.bean.NearbyPerson;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/2/24.
 */

public class NearbyPresenter extends NearbyContract.Presenter {

    public NearbyPresenter(NearbyContract.View mView) {
        super(mView);
    }

    @Override
    void nearbyList(int pageNo) {
        ApiRequest.nearbyList(pageNo, new HttpSubscriber() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.loadPersonError();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        JSONArray array = object.optJSONArray("list");
                        if (array != null) {
                            List<NearbyPerson> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.optJSONObject(i);
                                NearbyPerson person = NearbyPerson.parseBean(obj);
                                list.add(person);
                            }
                            if (mView != null)
                                mView.loadPersonSuccess(list);
                        } else {
                            if (mView != null)
                                mView.loadPersonError();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mView != null)
                        mView.loadPersonError();
                }
            }

        });
    }
}
