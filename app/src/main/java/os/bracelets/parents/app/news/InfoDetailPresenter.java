package os.bracelets.parents.app.news;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.bean.InfoDetail;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/3/21.
 */

public class InfoDetailPresenter extends InfoDetailContract.Presenter{


    public InfoDetailPresenter(InfoDetailContract.View mView) {
        super(mView);
    }

    @Override
    void loadInfoDetail(String infoId) {
        ApiRequest.infoDetail(infoId, new HttpSubscriber() {

            @Override
            public void onStart() {
                super.onStart();
                if(mView!=null)
                    mView.showLoading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(mView!=null)
                    mView.hideLoading();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if(mView!=null)
                    mView.hideLoading();
                if(result.code.equals(AppConfig.SUCCESS)){
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        InfoDetail detail = InfoDetail.parseBean(object);
                        if(mView!=null)
                            mView.loadSuccess(detail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
