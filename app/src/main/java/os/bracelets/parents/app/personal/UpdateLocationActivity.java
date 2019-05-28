package os.bracelets.parents.app.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.j256.ormlite.stmt.query.In;

import aio.health2world.utils.SPUtils;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * 更改位置
 */
public class UpdateLocationActivity extends AppCompatActivity implements View.OnClickListener,
        GeocodeSearch.OnGeocodeSearchListener {

    private AMap aMap;
    private MapView mapView;

    private TitleBar titleBar;

    private TextView tvLocation;

    private Button btnSure;

    private String longitude, latitude, location;

    private Marker screenMarker = null;
    private GeocodeSearch geocodeSearch;
    private LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);
        mapView = (MapView) findViewById(R.id.mapView);
        // 此方法必须重写
        mapView.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.titleBar);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        btnSure = (Button) findViewById(R.id.btnSure);

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        location = getIntent().getStringExtra("location");

        geocodeSearch = new GeocodeSearch(this);

        TitleBarUtil.setAttr(this, "", "位置", titleBar);
        tvLocation.setText(location);


        initMap();

        addListener();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        LatLng latLng = null;
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            double latitude1 = Double.parseDouble(latitude);
            double longitude1 = Double.parseDouble(longitude);
            latLng = new LatLng(latitude1, longitude1);
        } else {
            String longitude = (String) SPUtils.get(this, AppConfig.LONGITUDE, "");
            String latitude = (String) SPUtils.get(this, AppConfig.LATITUDE, "");
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                double latitude1 = Double.parseDouble(latitude);
                double longitude1 = Double.parseDouble(longitude);
                latLng = new LatLng(latitude1, longitude1);
            }
        }

        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18, 30, 30)));
        aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 13.5));
    }

    private void addListener() {
        btnSure.setOnClickListener(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkersToMap();
            }
        });

        // 设置可视范围变化时的回调的接口方法
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {
                //屏幕中心的Marker跳动
                startJumpAnimation();
                latLng = position.target;
                LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
                RegeocodeQuery query = new RegeocodeQuery(point, 100,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                geocodeSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
            }
        });
    }

    private void addMarkersToMap() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {
        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this, 125);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        } else {
            Log.e("amap", "screenMarker is null");
        }
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                tvLocation.setText(addressName);
            }
        } else {
            ToastUtil.showShort(rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSure:
                String location = tvLocation.getText().toString().trim();
                String longitude = String.valueOf(latLng.longitude);
                String latitude = String.valueOf(latLng.latitude);
                Intent intent = new Intent();
                intent.putExtra("location", location);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
    }
}
