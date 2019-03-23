package os.bracelets.parents.app.nearby;

import android.view.View;
import android.widget.TextView;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * 附近的人详情
 * Created by lishiyou on 2019/3/18.
 */

public class NearbyDetailActivity extends MVPBaseActivity {

    private TextView tvSex, tvBirthday, tvHeight, tvWeight;

    private int accountId;

    private TitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nearby_detail;
    }

    @Override
    protected void initView() {
        tvSex = findView(R.id.tvSex);
        tvBirthday = findView(R.id.tvBirthday);
        tvHeight = findView(R.id.tvHeight);
        tvWeight = findView(R.id.tvWeight);
        titleBar = findView(R.id.titleBar);
        TitleBarUtil.setAttr(this, "", "附近人资料", titleBar);
    }

    @Override
    protected void initData() {
        accountId = getIntent().getIntExtra("accountId", -1);
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

                }
            }
        });
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initListener() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
