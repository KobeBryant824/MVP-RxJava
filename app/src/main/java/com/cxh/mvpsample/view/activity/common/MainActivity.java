package com.cxh.mvpsample.view.activity.common;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cxh.mvpsample.R;
import com.cxh.mvpsample.base.BaseAutoActivity;
import com.cxh.mvpsample.manager.ActivityManager;
import com.cxh.mvpsample.util.ToastUtils;
import com.cxh.mvpsample.view.activity.XXXActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 简单的页面还是用mvc（欢迎页、关于页、无数据请求、以后版本无更新页面~~）
 *
 * 这个包下放mvc的activity
 */
@RuntimePermissions
public class MainActivity extends BaseAutoActivity {

    @BindString(R.string.app_name)
    String mAppName;

    @BindView(R.id.mvp_btn)
    Button mvpBtn;
    @BindView(R.id.permission_btn)
    Button permissionBtn;

    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public void RetryEvent() {

    }

    @Override
    protected void initViewsAndEvents() {

        // 解决Handler可能造成的内存泄漏，其实这段代码用handler也不会造成泄漏，hiahia~~
        Observable.timer(2, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                // Replace or Expand lambda , alt + enter
                .subscribe(aLong -> mPageStateManager.showContent());

        RxView.clicks(mvpBtn)
                .throttleFirst(2000, TimeUnit.MICROSECONDS)
                .subscribe(o -> startActivity(new Intent(MainActivity.this, XXXActivity.class)));

    }

    @OnClick({R.id.permission_btn})
    public void onClick(View view) {
        requestPermission();
    }

    private void requestPermission() {
        //M 才需要申请权限
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MainActivityPermissionsDispatcher.agreeWithCheck(this);
        }
    }

    private boolean doubleBackToExitPressedOnce = false;

    // 双击返回键退出
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityManager.getInstance().appExit();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "再次点击退出" + mAppName, Toast.LENGTH_SHORT).show();

        Observable.timer(2, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aLong -> doubleBackToExitPressedOnce = false);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void agree() {
        ToastUtils.showToast(this, "写SD卡限权已申请");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("这是申请写SD卡权限的说明....")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 再次执行请求
                    request.proceed();
                })
                .setNegativeButton("取消", (dialogInterface, i) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void permissionDenied() {
        ToastUtils.showToast(this, "权限被拒绝");
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void neverAskAgain() {
        Toast.makeText(this, "下次需要该权限请到系统设置中打开", Toast.LENGTH_SHORT).show();
    }
}