package com.wellsun.pushdoor.base;

import android.app.Activity;
import android.app.Application;

import com.cczhr.TTS;
import com.cczhr.TTSConstants;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

/**
 * date     : 2023-03-29
 * author   : ZhaoZheng
 * describe :
 */
public class App extends Application {
    /**
     * 记录当前栈里所有activity
     */
    private List<Activity> activities = new ArrayList<Activity>();
    public static TTS tts;
    public static App mApp;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp =this;
        initAndroidAutoSize();
        initTTS();
    }

    private void initTTS() {
        tts = TTS.getInstance();//获取单例对象
        tts.init(this, TTSConstants.TTS_NANNAN);//初始化
    }

    /**
     * 可以在 pt、in、mm 这三个冷门单位中，选择一个作为副单位，副单位是用于规避修改 DisplayMetrics#density 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响，使用副单位后可直接填写设计图上的像素尺寸，不需要再将像素转化为 dp
     */
    private void initAndroidAutoSize() {
        AutoSizeConfig.getInstance()
                .getUnitsManager()
                .setSupportDP(true)
                .setSupportSP(true)
                .setSupportSubunits(Subunits.MM);

    }


    /**
     * 添加了一个activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }


    /**
     * 结束指定activity 通过类名
     */
    public void finishActivityClass(Class<?> cls) {
        for (Activity temp : activities) {
            if (temp.getClass().equals(cls)) {
                finishActivity(temp);
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 保留指定activity 通过类名
     */
    public void keepOneActivityClass(Class<?> cls) {
        for (Activity temp : activities) {
            if (temp.getClass().equals(cls)) {
                finishActivity(temp);
            }
        }
    }

    /**
     * 保留指定的Activity
     */
    public void keepOneAcitivity(Activity keepActivity) {
        for (Activity activity : activities) {
            if (activity != null) {
                if (activity != keepActivity) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 应用退出，结束所有的activity
     */
    public void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

}
