package os.bracelets.parents.app.account;

import android.view.View;
import android.webkit.WebView;

import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

public class AgreementActivity extends BaseActivity {

    private WebView webView;

    private TitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_agreement;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        TitleBarUtil.setAttr(this,"","用户协议",titleBar);

        webView = findView(R.id.webView);
    }

    @Override
    protected void initData() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.zgznhh.com/Agreement.html");
//        webView.loadUrl("file:///android_asset/html/Agreement.html");
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
