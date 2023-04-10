package com.wellsun.pushdoor.util;

import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wellsun.pushdoor.R;

import java.util.Arrays;
import java.util.List;

/**
 * date     : 2023-03-22
 * author   : ZhaoZheng
 * describe :
 */
public class FragmentManagerHelper {
    // 管理类FragmentManager
    private FragmentManager mFragmentManager;
    // 容器布局id containerViewId
    private int mContainerViewId;

    /**
     * 构造函数
     *
     * @param fragmentManager 管理类FragmentManager
     * @param containerViewId 容器布局id containerViewId
     */
    public FragmentManagerHelper(@Nullable FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
    }

    /**
     * 添加Fragment
     */
    public void add(Fragment fragment, String... tag) {
        Log.v("内容", Arrays.asList(tag) + "");
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = mFragmentManager.beginTransaction();
        // 第一个参数是Fragment的容器id，需要添加的Fragment
        if (tag.length != 0) {
            fragmentTransaction.add(mContainerViewId, fragment, tag[0]);
        } else {
            fragmentTransaction.add(mContainerViewId, fragment);
        }
        // 一定要commit
        fragmentTransaction.commit();
    }


    /**
     * 切换显示Fragment
     */
    public void switchFragment(Fragment fragment, String... tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        // 1.先隐藏当前所有的Fragment
        List<Fragment> childFragments = mFragmentManager.getFragments();
        for (Fragment childFragment : childFragments) {
            fragmentTransaction.hide(childFragment);
        }
        // 2.如果容器里面没有我们就添加，否则显示
        if (childFragments.contains(fragment)) {
            fragmentTransaction.show(fragment);
        } else {
            if (tag.length != 0) {
                fragmentTransaction.add(mContainerViewId, fragment, tag[0]);
            } else {
                fragmentTransaction.add(mContainerViewId, fragment);
            }
        }
        // 替换Fragment
        // fragmentTransaction.replace(R.id.main_tab_fl,mHomeFragment);
        // 一定要commit
        fragmentTransaction.commit();
    }

    public Fragment getFrontFragment() {
        return mFragmentManager.findFragmentById(mContainerViewId);
    }

    public int getChooseFragment() {
        List<Fragment> childFragments = mFragmentManager.getFragments();
        for (int i = 0; i < childFragments.size(); i++) {
            boolean bBidden = childFragments.get(i).isHidden();
            if (!bBidden) {
                return i;
            }
        }
        return 0;
    }
}
