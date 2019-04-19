package os.bracelets.parents.app.nearby;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import aio.health2world.glide_transformations.CropCircleTransformation;
import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.NearbyPerson;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.AppUtils;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * 附近的人详情
 * Created by lishiyou on 2019/3/18.
 */

public class NearbyDetailActivity extends MVPBaseActivity {

    private TextView tvName, tvSex, tvBirthday, tvHeight, tvWeight;

    private ImageView ivImage;

    private Button btnChat;

    private TitleBar titleBar;

    private NearbyPerson person;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nearby_detail;
    }

    @Override
    protected void initView() {
        tvName = findView(R.id.tvName);
        tvSex = findView(R.id.tvSex);
        tvBirthday = findView(R.id.tvBirthday);
        tvHeight = findView(R.id.tvHeight);
        tvWeight = findView(R.id.tvWeight);
        titleBar = findView(R.id.titleBar);
        btnChat = findView(R.id.btnChat);
        ivImage = findView(R.id.ivImage);
        TitleBarUtil.setAttr(this, "", "附近人资料", titleBar);
    }

    @Override
    protected void initData() {
        person = (NearbyPerson) getIntent().getSerializableExtra("person");
        getPersonDetail(person.getAccountId());
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initListener() {
        btnChat.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
//        Intent intent = new Intent(this,ChatActivity.class);
//        intent.putExtra(EaseConstant.EXTRA_USER_ID,person.getPhone());
//        intent.putExtra(EaseConstant.EXTRA_USER_NICK,person.getNickName());
//        intent.putExtra(EaseConstant.EXTRA_USER_AVATAR,person.getProfile());
//        startActivity(intent);
    }

    private void initPersonMsg(NearbyPerson person){
        tvName.setText(person.getNickName());
        tvSex.setText(AppUtils.getSex(person.getSex()));
        tvHeight.setText(String.valueOf(person.getHeight())+"cm");
        tvWeight.setText(String.valueOf(person.getWeight())+"kg");
        tvBirthday.setText(person.getBirthday());
        Glide.with(mContext)
                .load(person.getProfile())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(ivImage);
    }

    private void getPersonDetail(int accountId) {
        ApiRequest.nearbyInfo(accountId, new HttpSubscriber() {
            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideLoading();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                hideLoading();
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        NearbyPerson person = NearbyPerson.parseBean(object);
                        initPersonMsg(person);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
