package os.bracelets.parents.app.news;

import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import aio.health2world.glide_transformations.CropCircleTransformation;
import aio.health2world.http.HttpResult;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.InfoDetail;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * 资讯详情
 * Created by lishiyou on 2019/2/24.
 */

public class InfoDetailActivity extends MVPBaseActivity<InfoDetailContract.Presenter> implements InfoDetailContract.View{

    public static final String INFO_ID = "infoId";

    private TitleBar titleBar;

    private String infoId = "";

    private TextView infoTitle,infoContent,infoTime,infoAuthor;

    private ImageView image;



    @Override
    protected InfoDetailContract.Presenter getPresenter() {
        return new InfoDetailPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_detail;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        infoTitle = findView(R.id.infoTitle);
        infoContent = findView(R.id.infoContent);
        infoTime = findView(R.id.infoTime);
        infoAuthor = findView(R.id.infoAuthor);
        image = findView(R.id.image);
    }

    @Override
    protected void initData() {
        infoId = getIntent().getStringExtra(INFO_ID);
        TitleBarUtil.setAttr(this, "", "资讯详情", titleBar);
        mPresenter.loadInfoDetail(infoId);
    }

    @Override
    public void loadSuccess(InfoDetail detail) {
        infoTitle.setText(detail.getTitle());
        infoTime.setText(detail.getCreateDate());
        infoAuthor.setText(detail.getAuthor());
        infoContent.setText(Html.fromHtml(detail.getContent()));
        Glide.with(mContext)
                .load(detail.getImageUrl())
                .placeholder(R.mipmap.bg_head)
                .error(R.mipmap.bg_head)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(image);
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
