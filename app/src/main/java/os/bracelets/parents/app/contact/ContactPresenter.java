package os.bracelets.parents.app.contact;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.bean.ContactBean;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;

/**
 * Created by lishiyou on 2019/2/24.
 */

public class ContactPresenter extends ContactContract.Presenter {

    public ContactPresenter(ContactContract.View mView) {
        super(mView);
    }

    @Override
    void contactList(int pageNo) {
        ApiRequest.contactList(pageNo, new HttpSubscriber() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (mView != null)
                    mView.loadContactError();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONArray array = new JSONArray(new Gson().toJson(result.data));
                        if (array != null) {
                            List<ContactBean> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.optJSONObject(i);
                                ContactBean contact = ContactBean.parseBean(object);
                                list.add(contact);
                            }
                            if (mView != null)
                                mView.loadContactSuccess(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mView != null)
                        mView.loadContactError();
                }
            }
        });
    }
}
