package os.bracelets.parents.app.personal;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.utils.Logger;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;

public class AmapSearchActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener,
        View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private PoiSearch.Query query;

    private String cityCode = "";


    private PoiSearch poiSearch;

    private List<PoiItem> poiItemList;

    private AmapSearchAdapter searchAdapter;

    private RecyclerView recyclerView;

    private ImageView ivBack;

    private EditText edKey;

    private TextView tvSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_search;
    }

    @Override
    protected void initView() {
        ivBack = findView(R.id.ivBack);
        edKey = findView(R.id.edKey);
        tvSearch = findView(R.id.tvSearch);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {

        poiItemList = new ArrayList<>();
        searchAdapter = new AmapSearchAdapter(poiItemList);
        searchAdapter.bindToRecyclerView(recyclerView);
        searchAdapter.setEmptyView(R.layout.layout_empty_view);

        cityCode = (String) SPUtils.get(this, AppConfig.CITY_CODE, "");

    }

    @Override
    protected void initListener() {
        ivBack.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        searchAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        List<PoiItem> list = poiResult.getPois();
        poiItemList.clear();
        poiItemList.addAll(list);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PoiItem poiItem = (PoiItem) adapter.getItem(position);

        LatLonPoint point = poiItem.getLatLonPoint();
        double latitude = point.getLatitude();
        double longitude = point.getLongitude();

        Logger.i("lsy", poiItem.getAdName() + poiItem.getBusinessArea() + poiItem.getCityName() + poiItem.getDirection()
                + poiItem.getProvinceName() + poiItem.getSnippet());

        Intent intent = new Intent();
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("location", poiItem.getCityName() + poiItem.getAdName() + poiItem.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {
            this.finish();
        }
        if (v.getId() == R.id.tvSearch) {
            String key = edKey.getText().toString().trim();
            query = new PoiSearch.Query(key, "", cityCode);
            query.setPageSize(20);// 设置每页最多返回多少条poiitem
            query.setPageNum(1);//设置查询页码
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        }
    }
}
