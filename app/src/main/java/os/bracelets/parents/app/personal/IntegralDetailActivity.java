package os.bracelets.parents.app.personal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import aio.health2world.DataEntity;
import aio.health2world.brvah.BaseQuickAdapter;
import aio.health2world.pickeview.OptionsPickerView;
import aio.health2world.pickeview.TimePickerView;
import aio.health2world.utils.DateUtil;
import aio.health2world.utils.TimePickerUtil;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

public class IntegralDetailActivity extends MVPBaseActivity<IntegralContract.Presenter> implements
        IntegralContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private TitleBar titleBar;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private IntegralDetailAdapter detailAdapter;

    private List<IntegralInfo> infoList;

    private TextView tvAllCount, startTime, endTime, type;

    private LinearLayout llStartTime, llEndTime, llType;

    private TimePickerView startPickView, endPickView;

    private OptionsPickerView optionsPickerView;

    private List<String> entityList = new ArrayList<>();

    private int mType = -1;
    private int pageIndex = 1;

    private String mStartTime = "", mEndTime = "";

//    private boolean[] typeShow = new boolean[]{true, true, true, false, false, false};
//
//    private Calendar calendar = Calendar.getInstance();

    @Override
    protected IntegralContract.Presenter getPresenter() {
        return new IntegralPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_integral_list;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        tvAllCount = findView(R.id.tvAllCount);
        llStartTime = findView(R.id.llStartTime);
        llEndTime = findView(R.id.llEndTime);
        llType = findView(R.id.llType);
        startTime = findView(R.id.startTime);
        endTime = findView(R.id.endTime);
        type = findView(R.id.type);
        refreshLayout = findView(R.id.refreshLayout);
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.appThemeColor));
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

        startPickView = TimePickerUtil.init(this, listenerStart);
        endPickView = TimePickerUtil.init(this, listenerEnd);


        entityList.add("全部");
        entityList.add("增加");
        entityList.add("消费");

        optionsPickerView = TimePickerUtil.initOptions(this, optionsSelectListener);
        optionsPickerView.setPicker(entityList);

        mPresenter.walletInfo();

        onRefresh();

    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        refreshLayout.setRefreshing(true);
        mPresenter.integralSerialList(mType, pageIndex, mStartTime, mEndTime);
    }

    @Override
    protected void initListener() {
        llStartTime.setOnClickListener(this);
        llEndTime.setOnClickListener(this);
        llType.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        detailAdapter.setOnLoadMoreListener(this, recyclerView);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private TimePickerView.OnTimeSelectListener listenerStart = new TimePickerView.OnTimeSelectListener() {
        @Override
        public void onTimeSelect(Date date, View v) {
            String start = DateUtil.getTime(date);
            String end = endTime.getText().toString();
            if (end.equals("--")) {
                startTime.setText(start);
                mStartTime = start;
            } else {
                long a = Long.parseLong(start.replace("-", ""));
                long b = Long.parseLong(end.replace("-", ""));
                if (a > b) {
                    ToastUtil.showLong("起止时间选择有误");
                } else {
                    startTime.setText(start);
                    mStartTime = start;
                    //刷新
                    onRefresh();
                }
            }

        }
    };

    private TimePickerView.OnTimeSelectListener listenerEnd = new TimePickerView.OnTimeSelectListener() {
        @Override
        public void onTimeSelect(Date date, View v) {
            String start = startTime.getText().toString();
            String end = DateUtil.getTime(date);
            if (start.equals("--")) {
                endTime.setText(end);
                mEndTime = end;
            } else {
                long a = Long.parseLong(start.replace("-", ""));
                long b = Long.parseLong(end.replace("-", ""));
                if (a > b) {
                    ToastUtil.showLong("起止时间选择有误");
                } else {
                    endTime.setText(end);
                    mEndTime = end;
                    //刷新
                    onRefresh();
                }
            }

        }
    };


    private OptionsPickerView.OnOptionsSelectListener optionsSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int options2, int options3, View v) {
            String s = entityList.get(options1);
            type.setText(s);
            if (s.equals("全部")) {
                mType = -1;
            } else if (s.equals("增加")) {
                mType = 0;
            } else {
                mType = 1;
            }
            onRefresh();
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.llStartTime:
                startPickView.show();
                break;
            case R.id.llEndTime:
                endPickView.show();
                break;
            case R.id.llType:
                optionsPickerView.show();
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        mPresenter.integralSerialList(mType, pageIndex, mStartTime, mEndTime);
    }

    @Override
    public void loadWalletInfoSuccess(WalletInfo info) {
        tvAllCount.setText(info.getIntegral() + " 积分");
    }

    @Override
    public void integralSuccess(List<IntegralInfo> list) {
        refreshLayout.setRefreshing(false);
        if (pageIndex == 1) {
            infoList.clear();
        }
        infoList.addAll(list);
        detailAdapter.notifyDataSetChanged();
        if (list.size() >= AppConfig.PAGE_SIZE)
            detailAdapter.loadMoreComplete();
        else
            detailAdapter.loadMoreEnd();
    }

    @Override
    public void integralError() {
        refreshLayout.setRefreshing(false);
    }
}
