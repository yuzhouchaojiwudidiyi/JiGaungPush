package com.wellsun.pushdoor.bean;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * date     : 2023-04-04
 * author   : ZhaoZheng
 * describe :
 */
public class ChooseViewBean {

    ImageView iv;
    TextView tv;
    Fragment fm;

    public ImageView getIv() {
        return iv;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public Fragment getFm() {
        return fm;
    }

    public void setFm(Fragment fm) {
        this.fm = fm;
    }

    public ChooseViewBean(ImageView iv, TextView tv, Fragment fm) {
        this.iv = iv;
        this.tv = tv;
        this.fm = fm;
    }


}
