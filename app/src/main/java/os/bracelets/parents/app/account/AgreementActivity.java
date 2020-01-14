package os.bracelets.parents.app.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import aio.health2world.http.HttpResult;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.MyWebView;
import os.bracelets.parents.view.TitleBar;

public class AgreementActivity extends BaseActivity {

    private WebView webView;

    private TitleBar titleBar;

    private LinearLayout layoutBottom;

    private Button btnDisAgree, btnAgree;

    private ProgressBar progress;

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
        progress = findView(R.id.progress);
        TitleBarUtil.setAttr(this, "", getString(R.string.user_agreement), titleBar);

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
//        webView.loadUrl("http://wx.zgznhh.com/Agreement.html");

        ApiRequest.helpUrl(new HttpSubscriber() {
            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        final String agreementUrl = object.optString("agreementUrl");
                        webView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                layoutBottom.setVisibility(View.VISIBLE);
                                webView.loadUrl(agreementUrl);
                            }
                        },500);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

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

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setProgress(newProgress);
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
    }

}
