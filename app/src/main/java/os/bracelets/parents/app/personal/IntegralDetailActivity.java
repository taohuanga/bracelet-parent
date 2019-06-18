package os.bracelets.parents.app.personal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aio.health2world.brvah.BaseQuickAdapter;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

public class IntegralDetailActivity extends MVPBaseActivity<IntegralContract.Presenter> implements
        IntegralContract.View{

    private TitleBar titleBar;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private IntegralDetailAdapter detailAdapter;

    private List<IntegralInfo> infoList;

    private TextView tvAllCount;

    private LinearLayout layoutIntegral;

    @Override
    protected IntegralContract.Presenter getPresenter() {
        return new IntegralPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_list;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        tvAllCount = findView(R.id.tvAllCount);
        layoutIntegral = findView(R.id.layoutIntegral);
        refreshLayout = findView(R.id.refreshLayout);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        refreshLayout.setEnabled(false);
        layoutIntegral.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "积分明细", titleBar);
        refreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.appThemeColor));
        infoList = new ArrayList<>();
        detailAdapter = new IntegralDetailAdapter(infoList);
        recyclerView.setAdapter(detailAdapter);
        detailAdapter.bindToRecyclerView(recyclerView);
        detailAdapter.setEmptyView(R.layout.layout_empty_view);

        mPresenter.integralSerialList();
    }

    @Override
    protected void initListener() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        detailAdapter.setOnLoadMoreListener(this,recyclerView);
    }

//    @Override
//    public void onLoadMoreRequested() {
//
//    }

    @Override
    public void loadWalletInfoSuccess(WalletInfo info) {
        tvAllCount.setText(info.getIntegral() + "积分");
    }

    @Override
    public void integralSuccess(List<IntegralInfo> list) {
        infoList.clear();
        infoList.addAll(list);
        detailAdapter.notifyDataSetChanged();
    }
}
