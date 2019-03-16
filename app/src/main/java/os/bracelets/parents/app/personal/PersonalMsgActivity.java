package os.bracelets.parents.app.personal;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.util.Date;

import aio.health2world.glide_transformations.CropCircleTransformation;
import aio.health2world.pickeview.TimePickerView;
import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.DateUtil;
import aio.health2world.utils.FilePathUtil;
import aio.health2world.utils.TimePickerUtil;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.R;
import os.bracelets.parents.app.about.FeedBackActivity;
import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;
import rx.functions.Action1;

/**
 * Created by lishiyou on 2019/2/23.
 */

public class PersonalMsgActivity extends MVPBaseActivity<PersonalMsgContract.Presenter>
        implements PersonalMsgContract.View, TimePickerView.OnTimeSelectListener {

    private String headImageUrl;

    public static final int ITEM_HEAD = 0x01;
    public static final int ITEM_NICK = 0x02;
    public static final int ITEM_NAME = 0x03;
    public static final int ITEM_SEX = 0x04;
    public static final int ITEM_BIRTHDAY = 0x05;
    public static final int ITEM_WEIGHT = 0x06;
    public static final int ITEM_HEIGHT = 0x07;
    public static final int ITEM_PHONE = 0x08;
    public static final int ITEM_LOCATION = 0x09;

    private TitleBar titleBar;

    private View layoutHeadImg, layoutNickName, layoutName, layoutSex, layoutBirthday, layoutWeight,
            layoutHeight, layoutPhone, layoutHomeAddress;

    private TextView tvNickName, tvName, tvSex, tvBirthday, tvWeight, tvHeight, tvPhone, tvHomeAddress;

    private ImageView ivHeadImg;

    private RxPermissions rxPermissions;

    private TimePickerView pickerView;

    @Override
    protected PersonalMsgContract.Presenter getPresenter() {
        return new PersonalMsgPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_msg;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);

        ivHeadImg = findView(R.id.ivHeadImg);
        tvNickName = findView(R.id.tvNickName);
        tvName = findView(R.id.tvName);
        tvSex = findView(R.id.tvSex);
        tvBirthday = findView(R.id.tvBirthday);
        tvWeight = findView(R.id.tvWeight);
        tvHeight = findView(R.id.tvHeight);
        tvPhone = findView(R.id.tvPhone);
        tvHomeAddress = findView(R.id.tvHomeAddress);

        layoutHeadImg = findView(R.id.layoutHeadImg);
        layoutNickName = findView(R.id.layoutNickName);
        layoutName = findView(R.id.layoutName);
        layoutSex = findView(R.id.layoutSex);
        layoutBirthday = findView(R.id.layoutBirthday);
        layoutWeight = findView(R.id.layoutWeight);
        layoutHeight = findView(R.id.layoutHeight);
        layoutPhone = findView(R.id.layoutPhone);
        layoutHomeAddress = findView(R.id.layoutHomeAddress);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "修改资料", titleBar);
        mPresenter.userInfo();
        rxPermissions = new RxPermissions(this);
        pickerView = TimePickerUtil.init(this, this);
    }


    @Override
    protected void initListener() {
        setOnClickListener(layoutHeadImg);
        setOnClickListener(layoutNickName);
        setOnClickListener(layoutName);
        setOnClickListener(layoutSex);
        setOnClickListener(layoutBirthday);
        setOnClickListener(layoutWeight);
        setOnClickListener(layoutHeight);
        setOnClickListener(layoutPhone);
        setOnClickListener(layoutHomeAddress);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.addAction(new TitleBar.TextAction("保存") {
            @Override
            public void performAction(View view) {
                saveMsg();
            }
        });
    }


    @Override
    public void onTimeSelect(Date date, View v) {
        String time = DateUtil.getTime(date);
        tvBirthday.setText(time);
    }

    @Override
    public void loadInfoSuccess(UserInfo info) {
        headImageUrl = info.getPortrait();
        Glide.with(this)
                .load(info.getPortrait())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(ivHeadImg);

        tvNickName.setText(info.getNickName());
        tvName.setText(info.getName());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layoutHeadImg:
                //修改头像
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                                    // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
                                    startActivityForResult(intent, ITEM_HEAD);
                                } else {
                                    ToastUtil.showShort("相关权限被拒绝");
                                }
                            }
                        });
                break;
            case R.id.layoutNickName:
                //修改昵称
                Intent intentNick = new Intent(this, InputMsgActivity.class);
                intentNick.putExtra(InputMsgActivity.KEY, "修改昵称");
                startActivityForResult(intentNick, ITEM_NICK);
                break;
            case R.id.layoutName:
                //修改真实姓名
                Intent intentName = new Intent(this, InputMsgActivity.class);
                intentName.putExtra(InputMsgActivity.KEY, "修改姓名");
                startActivityForResult(intentName, ITEM_NAME);
                break;
            case R.id.layoutSex:
                //修改性别
                break;
            case R.id.layoutBirthday:
                //修改生日
                pickerView.show();
                break;
            case R.id.layoutWeight:
                //修改身高
                break;
            case R.id.layoutHeight:
                //修改体重
                break;
            case R.id.layoutPhone:
                //修改手机号
                break;
            case R.id.layoutHomeAddress:
                //修改家庭住址
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case ITEM_HEAD:

                Uri uri = data.getData();
                if (uri != null) {
                    String imagePath = FilePathUtil.getRealPathFromURI(PersonalMsgActivity.this, uri);
                    if (!TextUtils.isEmpty(imagePath))
                        Glide.with(this)
                                .load(imagePath)
                                .placeholder(R.mipmap.ic_default_portrait)
                                .error(R.mipmap.ic_default_portrait)
                                .bitmapTransform(new CropCircleTransformation(mContext))
                                .into(ivHeadImg);
                    mPresenter.uploadImage(imagePath);
                }

                break;
            case ITEM_NICK:
                tvNickName.setText(data.getStringExtra("data"));
                break;
            case ITEM_NAME:
                tvName.setText(data.getStringExtra("data"));
                break;
        }
    }

    //保存资料
    private void saveMsg() {
        if (TextUtils.isEmpty(headImageUrl)) {
            ToastUtil.showShort("请先上传头像");
            return;
        }
        String nickName = tvNickName.getText().toString().trim();
        String name = tvName.getText().toString().trim();
        String birthday = tvBirthday.getText().toString().trim();
        mPresenter.updateMsg(headImageUrl, nickName, name, 0, birthday, "", "", "");
    }

    @Override
    public void updateMsgSuccess() {

    }

    @Override
    public void uploadImageSuccess(String imageUrl) {
        headImageUrl = imageUrl;
    }
}
