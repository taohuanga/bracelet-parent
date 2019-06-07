package os.bracelets.parents.app.personal;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

public class IntegralPresenter extends IntegralContract.Presenter {

    public IntegralPresenter(IntegralContract.View mView) {
        super(mView);
    }


    @Override
    void walletInfo() {
        ApiRequest.walletInfo(new HttpSubscriber() {
            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if(result.code.equals(AppConfig.SUCCESS)){
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        WalletInfo info = WalletInfo.parseBean(object);
                        if(mView!=null)
                            mView.loadWalletInfoSuccess(info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    void integralSerialList() {
        ApiRequest.integralSerialList(new HttpSubscriber() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if(result.code.equals(AppConfig.SUCCESS)){
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        JSONArray array = object.optJSONArray("list");
                        List<IntegralInfo> list = new ArrayList<>();
                        if(array!=null){
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.optJSONObject(i);
                                IntegralInfo info = IntegralInfo.parseBean(obj);
                                list.add(info);
                            }
                        }
                        if(mView!=null)
                            mView.integralSuccess(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
