package com.wellsun.pushdoor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.wellsun.pushdoor.socket.SocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * date     : 2023-04-03
 * author   : ZhaoZheng
 * describe :
 */
public class Test  extends Service {


    public static void main(String[] args){
        SocketClient socketClient = new SocketClient("192.168.5.106", "5556");

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Subscribe
    public void t(){

    }
}
