package os.bracelets.parents.app.nearby;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.NearbyPerson;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/24.
 */

public class NearbyActivity extends MVPBaseActivity<NearbyContract.Presenter> implements NearbyContract.View,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private TitleBar titleBar;

    private RecyclerView recyclerView;

    private SwipeRefreshLayout refreshLayout;

    private NearbyAdapter nearbyAdapter;

    private List<NearbyPerson> personList;

    private int pageNo = 1;


    @Override
    protected NearbyContract.Presenter getPresenter() {
        return new NearbyPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_list;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        refreshLayout = findView(R.id.refreshLayout);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "附近的人", titleBar);
        refreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.appThemeColor));
        personList = new ArrayList<>();
        nearbyAdapter = new NearbyAdapter(personList);
        recyclerView.setAdapter(nearbyAdapter);
        nearbyAdapter.bindToRecyclerView(recyclerView);
        nearbyAdapter.setEmptyView(R.layout.layout_empty_view);
        onRefresh();
    }


    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(this);
        nearbyAdapter.setOnItemClickListener(this);
        nearbyAdapter.setOnLoadMoreListener(this, recyclerView);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        refreshLayout.setRefreshing(true);
        mPresenter.nearbyList(pageNo);
    }

    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        mPresenter.nearbyList(pageNo);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        NearbyPerson person = (NearbyPerson) adapter.getItem(position);
//        Intent intent = new Intent(this, NearbyDetailActivity.class);
//        intent.putExtra("accountId", person.getAccountId());
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("userId",person.getPhone());
        startActivity(intent);
    }

    @Override
    public void loadPersonError() {
        if (pageNo > 1)
            pageNo--;
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void loadPersonSuccess(List<NearbyPerson> list) {
        refreshLayout.setRefreshing(false);
        if (pageNo == 1)
            personList.clear();
        personList.addAll(list);
        nearbyAdapter.notifyDataSetChanged();
        if (list.size() >= AppConfig.PAGE_SIZE)
            nearbyAdapter.loadMoreComplete();
        else {
            nearbyAdapter.loadMoreEnd();
        }
    }
}
