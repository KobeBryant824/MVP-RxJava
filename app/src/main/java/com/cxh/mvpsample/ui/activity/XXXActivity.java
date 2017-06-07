package com.cxh.mvpsample.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cxh.mvpsample.R;
import com.cxh.mvpsample.base.BaseActivity;
import com.cxh.mvpsample.contract.XXXContract;
import com.cxh.mvpsample.model.api.XXXApi;
import com.cxh.mvpsample.presenter.XXXPresenter;
import com.cxh.mvpsample.ui.activity.component.DaggerXXXComponent;
import com.cxh.mvpsample.ui.activity.moduel.XXXModuel;
import com.cxh.mvpsample.util.GlideUtils;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Desc:
 * Created by Hai (haigod7@gmail.com) on 2017/3/6 10:51 13:56.
 */
public class XXXActivity extends BaseActivity<XXXContract.Presenter> implements XXXContract.View {
    private static final String ACTION_CODE = "ilovekobebryant";
    private String path = "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-18-17882540_190116561497334_440657494176432128_n.jpg";
    @Inject
    XXXPresenter mPresenter;

    @BindView(R.id.first_tv)
    TextView firstTv;
    @BindView(R.id.showImage)
    ImageView showImage;

    @Override
    public int getLayoutID() {
        return R.layout.activity_xxx;
    }

    @Override
    protected XXXContract.Presenter initPresenter() {
        DaggerXXXComponent.builder().xXXModuel(new XXXModuel(this)).build().inject(this);// rebuild
        return mPresenter;
    }

    @Override
    protected void initViewsAndEvents() {

        GlideUtils.loadImage(path, showImage);

        String action = getIntent().getAction();
        if (action != null && action.equals(ACTION_CODE)) {
            showSnackbar(firstTv, "带参数的shortcuts");
        }
    }

    @Override
    public void RetryEvent() {
        mPageStateManager.showLoading();
        mPresenter.subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void setData(XXXApi.WelcomeEntity data) {
        firstTv.setText(data.toString());
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String loadUserId() {
        return null;
    }

    @Override
    public void showContent() {
        mPageStateManager.showContent();
    }

    @Override
    public void showError() {
        mPageStateManager.showError();
    }

    // fragment 会用到presenter
    @Override
    public void setPresenter(XXXPresenter presenter) {
        mPresenter = presenter;
    }
}