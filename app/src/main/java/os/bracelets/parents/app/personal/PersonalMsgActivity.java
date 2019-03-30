package os.bracelets.parents.app.personal;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aio.health2world.glide_transformations.CropCircleTransformation;
import aio.health2world.pickeview.OptionsPickerView;
import aio.health2world.pickeview.TimePickerView;
import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.DateUtil;
import aio.health2world.utils.FilePathUtil;
import aio.health2world.utils.SPUtils;
import aio.health2world.utils.TimePickerUtil;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.app.setting.UpdatePhoneActivity;
import os.bracelets.parents.bean.UserInfo;
import os.bracelets.parents.common.MVPActivity;
import os.bracelets.parents.utils.AppUtils;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;
import rx.functions.Action1;

/**
 * Created by lishiyou on 2019/2/23.
 */

public class PersonalMsgActivity extends MVPActivity<PersonalMsgContract.Presenter>
        implements PersonalMsgContract.View, TimePickerView.OnTimeSelectListener, OptionsPickerView.OnOptionsSelectListener {

    private String headImageUrl;

    public static final int ITEM_HEAD = 0x01;
    public static final int ITEM_NICK = 0x02;
    public static final int ITEM_NAME = 0x03;
    public static final int ITEM_SEX = 0x04;
    public static final int ITEM_BIRTHDAY = 0x05;
    public static final int ITEM_WEIGHT = 0x06;
    public static final int ITEM_HEIGHT = 0x07;
    public static final int ITEM_PHONE = 0x08;
    public static final int ITEM_ADDRESS = 0x09;

    private TitleBar titleBar;

    private View layoutHeadImg, layoutNickName, layoutName, layoutSex, layoutBirthday, layoutWeight,
            layoutHeight, layoutPhone, layoutHomeAddress;

    private TextView tvNickName, tvName, tvSex, tvBirthday, tvWeight, tvHeight, tvPhone, tvHomeAddress,
            tvLongitude, tvLatitude;

    private ImageView ivHeadImg;

    private RxPermissions rxPermissions;

    private TimePickerView pickerView;

    private OptionsPickerView optionsPicker;

    private List<String> listSex = new ArrayList<>();

    private MapView mapView;
    private AMap aMap;

    @Override
    protected PersonalMsgContract.Presenter getPresenter() {
        return new PersonalMsgPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_msg);

        mapView = (MapView) findViewById(R.id.mapView);
//        // 此方法必须重写
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.titleBar);

        ivHeadImg = (ImageView) findViewById(R.id.ivHeadImg);
        tvNickName = (TextView) findViewById(R.id.tvNickName);
        tvName = (TextView) findViewById(R.id.tvName);
        tvSex = (TextView) findViewById(R.id.tvSex);
        tvBirthday = (TextView) findViewById(R.id.tvBirthday);
        tvWeight = (TextView) findViewById(R.id.tvWeight);
        tvHeight = (TextView) findViewById(R.id.tvHeight);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvHomeAddress = (TextView) findViewById(R.id.tvHomeAddress);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);

        layoutHeadImg = findViewById(R.id.layoutHeadImg);
        layoutNickName = findViewById(R.id.layoutNickName);
        layoutName = findViewById(R.id.layoutName);
        layoutSex = findViewById(R.id.layoutSex);
        layoutBirthday = findViewById(R.id.layoutBirthday);
        layoutWeight = findViewById(R.id.layoutWeight);
        layoutHeight = findViewById(R.id.layoutHeight);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutHomeAddress = findViewById(R.id.layoutHomeAddress);
    }

    private void initData() {
        TitleBarUtil.setAttr(this, "", "修改资料", titleBar);

        rxPermissions = new RxPermissions(this);
        pickerView = TimePickerUtil.init(this, this);
        optionsPicker = TimePickerUtil.initOptions(this, this);
        listSex.add("男");
        listSex.add("女");
        optionsPicker.setPicker(listSex);

        mPresenter.userInfo();
    }


    private void initListener() {
        layoutHeadImg.setOnClickListener(this);
        layoutNickName.setOnClickListener(this);
        layoutName.setOnClickListener(this);
        layoutSex.setOnClickListener(this);
        layoutBirthday.setOnClickListener(this);
        layoutWeight.setOnClickListener(this);
        layoutHeight.setOnClickListener(this);
        layoutPhone.setOnClickListener(this);
        layoutHomeAddress.setOnClickListener(this);
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

    private void showMap(double latitude, double longitude) {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        //设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 17.5));
        LatLng latLng = new LatLng(latitude, longitude);
        Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("")
                .zIndex(2).snippet("DefaultMarker"));
    }

    @Override
    public void onTimeSelect(Date date, View v) {
        String time = DateUtil.getTime(date);
        tvBirthday.setText(time);
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        tvSex.setText(listSex.get(options1));
    }

    @Override
    public void loadInfoSuccess(UserInfo info) {
        headImageUrl = info.getPortrait();
        tvNickName.setText(info.getNickName());
        tvName.setText(info.getName());
        tvSex.setText(AppUtils.getSex(info.getSex()));
        tvBirthday.setText(info.getBirthday());
        tvWeight.setText(info.getWeight());
        tvHeight.setText(info.getHeight());
        tvPhone.setText(info.getPhone());
        tvLongitude.setText(info.getLongitude());
        tvLatitude.setText(info.getLatitude());
        tvHomeAddress.setText(info.getLocation());

        Glide.with(this)
                .load(info.getPortrait())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(PersonalMsgActivity.this))
                .into(ivHeadImg);
        if (!TextUtils.isEmpty(info.getLatitude()) && !TextUtils.isEmpty(info.getLongitude())) {
//            showMap(Double.parseDouble((String) SPUtils.get(this, AppConfig.LATITUDE, "")),
//                    Double.parseDouble((String) SPUtils.get(this, AppConfig.LONGITUDE, "")));
            showMap(Double.parseDouble(info.getLatitude()), Double.parseDouble(info.getLongitude()));
        }

    }


    @Override
    public void updateMsgSuccess() {
//        String userNick = tvNickName.getText().toString();
//        manager.getCurrentUserInfo().setNickname(userNick);
    }

    @Override
    public void uploadImageSuccess(String imageUrl) {
        headImageUrl = imageUrl;
        //修改环信头像
//        manager.getCurrentUserInfo().setAvatar(headImageUrl);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        pickerView = null;
    }

    @Override
    public void onClick(View v) {
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
                optionsPicker.show();
                break;
            case R.id.layoutBirthday:
                //修改生日
                pickerView.show();
                break;
            case R.id.layoutHeight:
                //修改身高
                Intent intentHeight = new Intent(this, InputMsgActivity.class);
                intentHeight.putExtra(InputMsgActivity.KEY, "修改身高");
                intentHeight.putExtra(InputMsgActivity.TYPE, ITEM_HEIGHT);
                startActivityForResult(intentHeight, ITEM_HEIGHT);
                break;
            case R.id.layoutWeight:
                //修改体重
                Intent intentWeight = new Intent(this, InputMsgActivity.class);
                intentWeight.putExtra(InputMsgActivity.KEY, "修改体重");
                intentWeight.putExtra(InputMsgActivity.TYPE, ITEM_WEIGHT);
                startActivityForResult(intentWeight, ITEM_WEIGHT);
                break;
            case R.id.layoutPhone:
                //修改手机号
                Intent intentPhone = new Intent(this, UpdatePhoneActivity.class);
                intentPhone.putExtra("phone", tvPhone.getText().toString());
                startActivityForResult(intentPhone, ITEM_PHONE);
                break;
            case R.id.layoutHomeAddress:
                //修改家庭住址
                Intent intentAddress = new Intent(this, InputMsgActivity.class);
                intentAddress.putExtra(InputMsgActivity.KEY, "修改住址");
                startActivityForResult(intentAddress, ITEM_ADDRESS);
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
                                .bitmapTransform(new CropCircleTransformation(PersonalMsgActivity.this))
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
            case ITEM_WEIGHT:
                tvWeight.setText(data.getStringExtra("data"));
                break;
            case ITEM_HEIGHT:
                tvHeight.setText(data.getStringExtra("data"));
                break;
            case ITEM_PHONE:
                tvPhone.setText(data.getStringExtra("newPhone"));
                break;
            case ITEM_ADDRESS:
                tvHomeAddress.setText(data.getStringExtra("data"));
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
        if (TextUtils.isEmpty(nickName)) {
            ToastUtil.showShort("昵称不能为空");
            return;
        }
        //0未知 1男 2女
        String sex = tvSex.getText().toString().trim();
        if (TextUtils.isEmpty(sex)) {
            ToastUtil.showShort("性别不能为空");
            return;
        }
        int sexType = 0;
        if (sex.equals("男")) {
            sexType = 1;
        } else if (sex.equals("女")) {
            sexType = 2;
        }
        String realName = tvName.getText().toString().trim();
        String birthday = tvBirthday.getText().toString().trim();
        String height = tvHeight.getText().toString().trim();
        String weight = tvWeight.getText().toString().trim();
        String address = tvHomeAddress.getText().toString().trim();
        mPresenter.updateMsg(headImageUrl, nickName, realName, sexType, birthday, height, weight, address);
    }

}
