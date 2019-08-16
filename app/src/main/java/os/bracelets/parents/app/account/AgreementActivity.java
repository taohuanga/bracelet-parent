package os.bracelets.parents.app.account;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;

import android.widget.Button;
import android.widget.LinearLayout;

import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.MyWebView;
import os.bracelets.parents.view.TitleBar;

public class AgreementActivity extends BaseActivity {

    private MyWebView webView;

    private TitleBar titleBar;

    private LinearLayout layoutBottom;

    private Button btnDisAgree, btnAgree;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_agreement;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        btnAgree = findView(R.id.btnAgree);
        btnDisAgree = findView(R.id.btnDisAgree);
        layoutBottom = findView(R.id.layoutBottom);
        TitleBarUtil.setAttr(this, "", "用户协议", titleBar);

        webView = findView(R.id.webView);
    }

    @Override
    protected void initData() {
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDisplayZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.loadUrl("http://www.zgznhh.com/Agreement.html");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnDisAgree:
                this.finish();
                break;
            case R.id.btnAgree:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected void initListener() {
        btnAgree.setOnClickListener(this);
        btnDisAgree.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.setOnCustomScrollChangeListener(new MyWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
                //WebView的总高度
                float webViewContentHeight = webView.getContentHeight() * webView.getScale();
                Log.i("lsy", "webViewContentHeight=" + webViewContentHeight + "l=" + l + ",t=" + t + ",oldl=" + oldl + ",oldt=" + oldt);
                //WebView的现高度
                float webViewCurrentHeight = (webView.getHeight() + webView.getScrollY());
                if ((webViewContentHeight - webViewCurrentHeight) <= 20) {
                    layoutBottom.setVisibility(View.VISIBLE);
                } else {
                    layoutBottom.setVisibility(View.GONE);
                }
            }
        });
    }

}
