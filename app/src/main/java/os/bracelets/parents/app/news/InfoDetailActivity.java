package os.bracelets.parents.app.news;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * 资讯详情
 * Created by lishiyou on 2019/2/24.
 */

public class InfoDetailActivity extends BaseActivity {

    private TitleBar titleBar;

    private WebView webView;

    public static final String URL = "https://www.baidu.com/";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_detail;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        webView = findView(R.id.webView);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "资讯详情", titleBar);
        initWebView();
        webView.loadUrl(URL);
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webView必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    @Override
    protected void initListener() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){


        });
    }
}
