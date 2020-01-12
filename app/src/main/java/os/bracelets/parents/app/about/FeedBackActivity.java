package os.bracelets.parents.app.about;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import aio.health2world.http.HttpResult;
import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.FilePathUtil;
import aio.health2world.utils.ToastUtil;
import aio.health2world.view.LoadingDialog;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.common.BasePresenter;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.ImageBase64;
import os.bracelets.parents.utils.StringUtils;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;
import rx.functions.Action1;

/**
 * Created by lishiyou on 2019/2/23.
 */

public class FeedBackActivity extends BaseActivity {

    private TitleBar titleBar;

    private ImageView imageView;

    private Button btnSubmit;

    public static final int REQUEST_CODE_PHOTO = 0x01;

    private RxPermissions rxPermissions;

    private String imagePath = "";

    private EditText edTitle, edContent;

    private LoadingDialog dialog;

//    private RecyclerView recyclerView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        imageView = findView(R.id.imageView);
        btnSubmit = findView(R.id.btnSubmit);
        edTitle = findView(R.id.edTitle);
        edContent = findView(R.id.edContent);
//        recyclerView = findView(R.id.recyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", getString(R.string.feedback), titleBar);
        rxPermissions = new RxPermissions(this);
        dialog = new LoadingDialog(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.imageView:
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                                    // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
                                    startActivityForResult(intent, REQUEST_CODE_PHOTO);
                                } else {
                                    ToastUtil.showShort(getString(R.string.permission_denied));
                                }
                            }
                        });
                break;
            case R.id.btnSubmit:
                String title = edTitle.getText().toString().trim();
                String content = edContent.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    ToastUtil.showShort(getString(R.string.input_title));
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(getString(R.string.input_content));
                    return;
                }
                if (TextUtils.isEmpty(imagePath)) {
                    feedBack(title, content, "");
                } else {
                    uploadImage(imagePath);
                }
                break;
        }
    }

    @Override
    protected void initListener() {
        imageView.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    imagePath = FilePathUtil.getRealPathFromURI(FeedBackActivity.this, uri);
                    Bitmap bit = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
                    imageView.setImageBitmap(bit);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path))
            return;
        File file = new File(path);
        if (!file.exists())
            return;
        String imageKey = ImageBase64.imageConvertBase64(path);
        ApiRequest.uploadImage(1, imageKey, new HttpSubscriber() {

            @Override
            public void onStart() {
                super.onStart();
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(result.data));
                        String serverPath = object.optString("data");
                        feedBack(edTitle.getText().toString(), edContent.getText().toString(), serverPath);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort(result.errorMessage);
                }
            }
        });
    }

    private void feedBack(String title, String content, String serverPath) {
        ApiRequest.feedBack(title, content, serverPath, new HttpSubscriber() {

            @Override
            public void onStart() {
                super.onStart();
                if (dialog != null && !dialog.isShowing())
                    dialog.show();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onNext(HttpResult result) {
                super.onNext(result);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                ToastUtil.showShort(result.errorMessage);
                if (result.code.equals(AppConfig.SUCCESS)) {
                    finish();
                }
            }
        });
    }
}
