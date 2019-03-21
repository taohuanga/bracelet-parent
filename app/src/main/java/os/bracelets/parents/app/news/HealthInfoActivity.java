package os.bracelets.parents.app.news;

import android.content.Intent;
import android.support.v4.util.TimeUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.utils.DateUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.HealthInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/21.
 */

public class HealthInfoActivity extends MVPBaseActivity<HealthInfoContract.Presenter> implements HealthInfoContract.View,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private TitleBar titleBar;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private HealthInfoAdapter infoAdapter;

    private List<HealthInfo> infoList;

    private int pageNo = 1;

    //0历史  1 最新
    private int infoType = 1;

    private String releaseTime="";

    @Override
    protected HealthInfoContract.Presenter getPresenter() {
        return new HealthInfoPresenter(this);
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
//        releaseTime = DateUtil.getCurrentTime(new Date(System.currentTimeMillis()));
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "资讯列表", titleBar);
        refreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.appThemeColor));

        infoList = new ArrayList<>();
        infoAdapter = new HealthInfoAdapter(infoList);
        recyclerView.setAdapter(infoAdapter);
        infoAdapter.bindToRecyclerView(recyclerView);
        infoAdapter.setEmptyView(R.layout.layout_empty_view);

        onRefresh();
    }


    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(this);
        infoAdapter.setOnItemClickListener(this);
        infoAdapter.setOnLoadMoreListener(this, recyclerView);
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
        infoType = 1;
        refreshLayout.setRefreshing(true);
//        releaseTime = DateUtil.getCurrentTime(new Date(System.currentTimeMillis()));
        mPresenter.informationList(infoType, pageNo, releaseTime);
    }

    @Override
    public void onLoadMoreRequested() {
        pageNo = 1;
        infoType = 0;
//        releaseTime = infoList.get(infoList.size()-1).getCreateDate();
        mPresenter.informationList(infoType, pageNo, releaseTime);
    }

    @Override
    public void loadInfoSuccess(List<HealthInfo> list) {
        refreshLayout.setRefreshing(false);
        if (pageNo == 1)
            infoList.clear();
        infoList.addAll(list);
        infoAdapter.notifyDataSetChanged();

        if (list.size() >= AppConfig.PAGE_SIZE)
            infoAdapter.loadMoreComplete();
        else
            infoAdapter.loadMoreEnd();
    }

    @Override
    public void loadInfoError() {
        refreshLayout.setRefreshing(false);
        if (pageNo > 1)
            pageNo--;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        HealthInfo info = (HealthInfo) adapter.getItem(position);
        Intent intent = new Intent(this,InfoDetailActivity.class);
        intent.putExtra(InfoDetailActivity.INFO_ID,info.getInformationId());
        startActivity(intent);
    }
}
