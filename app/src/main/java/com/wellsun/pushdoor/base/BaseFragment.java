package com.wellsun.pushdoor.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wellsun.pushdoor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * content:
 *
 * @date: 2021/6/22
 * @author: ZhaoZheng
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    public BaseActivity mActivity;
    public LayoutInflater mInflater;
    public Bundle mBundle;
    public View mView;


    @Subscribe
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = (BaseActivity) getActivity();
        this.mBundle = getArguments();
        if (!EventBus.getDefault().isRegistered(this)) {//是否注册eventbus的判断
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(getLayoutId(), container, false);
        }
        mInflater = inflater;
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * 加载页面布局
     */
    public abstract int getLayoutId();

    /**
     * 初始化方法
     */
    public void init() {
        initView();
        setListener();
        initData();
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int id) {
        if (mView == null) {
            return null;
        }
        return (T) mView.findViewById(id);
    }

    /**
     * 初始化布局控件
     */
    public abstract void initView();

    /**
     * 初始化设置监听
     */
    public abstract void setListener();

    /**
     * 初始化数据
     */
    public abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
