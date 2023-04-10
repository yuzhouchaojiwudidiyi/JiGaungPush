package com.wellsun.pushdoor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wellsun.pushdoor.base.BaseActivity;
import com.wellsun.pushdoor.bean.ChooseViewBean;
import com.wellsun.pushdoor.fragment.FaultFragment;
import com.wellsun.pushdoor.fragment.FaultRepairFragment;
import com.wellsun.pushdoor.fragment.UserFragment;
import com.wellsun.pushdoor.interfac.NotifyListener;
import com.wellsun.pushdoor.service.NotifyService;
import com.wellsun.pushdoor.socket.SocketClient;
import com.wellsun.pushdoor.util.FragmentManagerHelper;
import com.wellsun.pushdoor.util.ToastPrint;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private android.widget.FrameLayout fl_container;
    private android.widget.LinearLayout llFault;
    private android.widget.ImageView ivFault;
    private android.widget.TextView tvFault;
    private android.widget.LinearLayout llFaultRepair;
    private android.widget.ImageView ivFaultRepair;
    private android.widget.TextView tvFaultRepair;
    private android.widget.LinearLayout llUser;
    private android.widget.ImageView ivUser;
    private android.widget.TextView tvUser;
    private FragmentManagerHelper fragmentManagerHelper;
    ArrayList<View> moduleList = new ArrayList<>();
    private FaultFragment faultFragment;
    private UserFragment userFragment;
    private FaultRepairFragment faultRepairFragment;
    private int screenWidth;
    private Map<Integer, ChooseViewBean> mapViewSelector;

    NotifyService.MyBinder binder = null;
    private NotifyService serviceNotify;

    //通过实现ServiceConnection来处理和service连接 可以获取service引用 可以把activity引用传递过去
    class NotifyServiceConn implements ServiceConnection, NotifyListener {
        // 服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //service为onBind方法返回的Service实例
            binder = (NotifyService.MyBinder) service;
            serviceNotify = binder.getService();
            serviceNotify.setMainActivity(MainActivity.this); //把当前对象传递给myservice
            serviceNotify.setOnProgressBarListener(this);

        }

        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }

        @Override
        public void notifyTip(String tip) {

        }
    }


    @Override
    public void initData() {
        initFragment();
        getWide();
        Intent intentService = new Intent(mContext, NotifyService.class);
//        startService(intent);
        //绑定服务
        NotifyServiceConn notifyServiceConn = new NotifyServiceConn();
        bindService(intentService, notifyServiceConn, Context.BIND_AUTO_CREATE);

        //解绑服务
//        unbindService(notifyServiceConn);
        startService(new Intent(mContext, Test.class));

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        fl_container = (FrameLayout) findViewById(R.id.fl_container);
        llFault = (LinearLayout) findViewById(R.id.ll_fault);
        ivFault = (ImageView) findViewById(R.id.iv_fault);
        tvFault = (TextView) findViewById(R.id.tv_fault);
        llFaultRepair = (LinearLayout) findViewById(R.id.ll_fault_repair);
        ivFaultRepair = (ImageView) findViewById(R.id.iv_fault_repair);
        tvFaultRepair = (TextView) findViewById(R.id.tv_fault_repair);
        llUser = (LinearLayout) findViewById(R.id.ll_user);
        ivUser = (ImageView) findViewById(R.id.iv_user);
        tvUser = (TextView) findViewById(R.id.tv_user);
        moduleList.add(ivFault);
        moduleList.add(ivFaultRepair);
        moduleList.add(ivUser);
        moduleList.add(tvFault);
        moduleList.add(tvFaultRepair);
        moduleList.add(tvUser);
    }

    @Override
    public void setListener() {
        fl_container.setOnTouchListener(mOnTouchListener);
    }

    private void getWide() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        Log.v("滑动", "screenWidth=" + screenWidth);
    }

    private void initFragment() {
        faultFragment = new FaultFragment();
        faultRepairFragment = new FaultRepairFragment();
        userFragment = new UserFragment();
        fragmentManagerHelper = new FragmentManagerHelper(getSupportFragmentManager(), R.id.fl_container);
        fragmentManagerHelper.add(faultFragment);
        tvFault.setSelected(true);
        ivFault.setSelected(true);

        //滑动选中监听
        mapViewSelector = new LinkedHashMap<>();
        mapViewSelector.put(0, new ChooseViewBean(ivFault, tvFault, faultFragment));
        mapViewSelector.put(1, new ChooseViewBean(ivFaultRepair, tvFaultRepair, faultRepairFragment));
        mapViewSelector.put(2, new ChooseViewBean(ivUser, tvUser, userFragment));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_fault:
                selectorModule(ivFault, tvFault);
                fragmentManagerHelper.switchFragment(faultFragment);

                break;
            case R.id.ll_fault_repair:
                selectorModule(ivFaultRepair, tvFaultRepair);
                fragmentManagerHelper.switchFragment(faultRepairFragment);

                break;
            case R.id.ll_user:
                selectorModule(ivUser, tvUser);
                fragmentManagerHelper.switchFragment(userFragment);

                break;
        }

    }

    private void selectorModule(ImageView iv, TextView tv) {
        for (int i = 0; i < moduleList.size(); i++) {
            moduleList.get(i).setSelected(false);
        }
        iv.setSelected(true);
        tv.setSelected(true);
    }

    //滑动选择
    boolean scrollX = false;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        private float mDownX;
        private float mDownY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    scrollX = true;
                    mDownX = event.getX();
                    mDownY = event.getY();
                    Log.v("滑动", "点击=" + mDownX);
                    Log.v("滑动", "点击y=" + mDownY);
                    break;
                case MotionEvent.ACTION_UP:
                    scrollX = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - mDownX;
                    float deltaY = event.getY() - mDownY;
                    // 在这里处理滑动事件
                    Log.v("滑动", "滑动x=" + deltaX);
                    if (scrollX) {
                        if (Math.abs(deltaX) > screenWidth / 7) {
                            scrollX = false;
                            int chooseFragment = fragmentManagerHelper.getChooseFragment();
                            if (deltaX < 0) {    //正向
                                if (chooseFragment == mapViewSelector.size() - 1) {  //最右边继续右滑
                                    //scrollChooseView(0);
                                } else {
                                    scrollChooseView(chooseFragment + 1);
                                }
                            } else {             //反向
                                if (chooseFragment == 0) {                           //最左边继续左滑
                                    //scrollChooseView(mapViewSelector.size() - 1);
                                } else {
                                    scrollChooseView(chooseFragment - 1);
                                }
                            }
                        }
                    }
                    break;
            }
            return true;
        }
    };

    private void scrollChooseView(int i) {
        ChooseViewBean chooseViewBean = mapViewSelector.get(i);
        selectorModule(chooseViewBean.getIv(), chooseViewBean.getTv());
        fragmentManagerHelper.switchFragment(chooseViewBean.getFm());
    }
}
