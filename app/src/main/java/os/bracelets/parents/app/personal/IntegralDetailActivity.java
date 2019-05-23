package os.bracelets.parents.app.personal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import os.bracelets.parents.R;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

public class IntegralDetailActivity extends MVPBaseActivity<IntegralContract.Presenter> implements IntegralContract.View {

    private TitleBar titleBar;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private IntegralDetailAdapter detailAdapter;

    private List<IntegralInfo> infoList;

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
        refreshLayout = findView(R.id.refreshLayout);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        refreshLayout.setEnabled(false);
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
    }

    @Override
    public void integralSuccess(List<IntegralInfo> list) {
        infoList.clear();
        infoList.addAll(list);
        detailAdapter.notifyDataSetChanged();
    }
}
