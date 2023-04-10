package com.wellsun.pushdoor.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.wellsun.pushdoor.R;
import com.wellsun.pushdoor.activity.NotifycationActivity;
import com.wellsun.pushdoor.base.App;
import com.wellsun.pushdoor.interfac.NotifyListener;
import com.wellsun.pushdoor.socket.SocketClient;
import com.wellsun.pushdoor.util.ToastPrint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * date     : 2023-03-29
 * author   : ZhaoZheng
 * describe :
 */
public class NotifyService extends Service implements SocketClient.SocketListener {
    String ip = "192.168.5.106";
    String port = "5556";
    Activity activity;

    //必须实现的方法，用户返回Binder对象
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    //获取服务引用
    public class MyBinder extends Binder {
        public NotifyService getService() {
            return NotifyService.this;
        }
    }

    //每次启动Servcie时都会调用该方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //解绑Servcie调用该方法
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    //退出或者销毁时调用该方法
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //传过来的activity的引用
    public void setMainActivity(Activity activity) {
        this.activity = activity;
    }

    NotifyListener notifyListener;

    public void setOnProgressBarListener(NotifyListener listener) {
        this.notifyListener = listener;
    }


    //创建Service时调用该方法，只调用一次
    @Override
    public void onCreate() {
        super.onCreate();
        SocketClient socketClient = new SocketClient(ip, port);
        socketClient.setOnConnectListener(this);
        socketClient.connect();



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    socketClient.send("发送");
                    SystemClock.sleep(10000);
                }
            }
        }).start();

    }
    private void showNotifycation(String strChinese_gbk) {
        Intent intent = new Intent(this, NotifycationActivity.class);
        //用intent表现出我们要启动Notification的意图
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        //将Intent对象传入PendingIntent对象的getActivity方法中
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("This is content title")
                //设置通知栏中的标题
                .setContentText(strChinese_gbk + "")
                //设置通知栏中的内容
                .setWhen(System.currentTimeMillis())
                //设置通知出现的时间，此时为事件响应后立马出现通知
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知出现在手机顶部的小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置通知栏中的大图标
                .setContentIntent(pi)
                //将PendingIntent对象传入该方法中，表明点击通知后进入到NotificationActivity.class页面
                .setAutoCancel(true)
                //点击通知后，通知自动消失
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //默认选项，根据手机当前的环境来决定通知发出时播放的铃声，震动，以及闪光灯
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //设置通知的权重
                .build();
        manager.notify(1, notification);
        //用于显示通知，第一个参数为id，每个通知的id都必须不同。第二个参数为具体的通知对象

    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceived(String message) {
        App.tts.speakText(message);
        showNotifycation(message);
        notifyListener.notifyTip(message);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastPrint.showText(message);
            }
        });
    }

    @Override
    public void onSent(String message) {

    }
}
