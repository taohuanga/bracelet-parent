package os.bracelets.parents.app.main;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aio.health2world.http.CommonService;
import aio.health2world.http.HttpResult;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import okhttp3.ResponseBody;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.RemindBean;
import os.bracelets.parents.bean.WeatherInfo;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.http.ServiceFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by lishiyou on 2019/1/27.
 */

public class MainPresenter extends MainContract.Presenter {


    public MainPresenter(MainContract.View mView) {
        super(mView);
    }

    @Override
    void homeMsg() {
        ApiRequest.homeMsg(new HttpSubscriber() {
            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        int stepNum = object.optInt("stepNum", 0);
                        JSONArray array = object.optJSONArray("remindList");
                        List<RemindBean> list = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.optJSONObject(i);
                            RemindBean remind = RemindBean.parseBean(obj);
                            list.add(remind);
                        }
                        if (mView != null)
                            mView.loadMsgSuccess(stepNum, list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    @Override
//    void remindList() {
//        ApiRequest.remindList(new HttpSubscriber() {
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//            }
//
//            @Override
//            public void onNext(HttpResult result) {
//                super.onNext(result);
//                if (result.code.equals(AppConfig.SUCCESS)) {
//                    try {
//                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
//                        JSONArray array = object.optJSONArray("list");
//                        if (array != null) {
//                            List<RemindBean> list = new ArrayList<>();
//                            for (int i = 0; i < array.length(); i++) {
//                                JSONObject obj = array.optJSONObject(i);
//                                RemindBean remind = RemindBean.parseBean(obj);
//                                list.add(remind);
//                            }
//                            if (mView != null)
//                                mView.loadRemindSuccess(list);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    @Override
    void getWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restapi.amap.com/")
                .build();
        Map<String, String> map = new HashMap<>();
        map.put("key", "de0e5942b4033a3d6cfa3a0d31a0c756");
        map.put("city", (String) SPUtils.get(MyApplication.getInstance(), AppConfig.CITY_CODE, ""));
        map.put("extensions", "base");
        map.put("output", "JSON");

        retrofit
                .create(CommonService.class)
                .getWeather(map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String data = response.body().string();
                            JSONObject obj = new JSONObject(data);
                            String result = obj.optString("info");
                            if (result.equals("OK")) {
                                JSONArray array = obj.optJSONArray("lives");
                                JSONObject object = array.getJSONObject(0);
                                WeatherInfo info = WeatherInfo.parseBean(object);
                                if (mView != null) {
                                    mView.loginWeatherSuccess(info);
                                }
                            }
                            Logger.i("lsy", data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                    }
                });
    }

    @Override
    void loginHx(BaseInfo info) {
        EMClient.getInstance()
                .login(info.getPhone(), info.getPhone(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Logger.i("hx", "login success");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Logger.i("hx", "login failed " + s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
    }
}
