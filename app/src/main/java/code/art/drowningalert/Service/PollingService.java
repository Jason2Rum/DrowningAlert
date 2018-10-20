package code.art.drowningalert.Service;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import code.art.drowningalert.LocationFragment;
import code.art.drowningalert.MainActivity;
import code.art.drowningalert.MonitorTask;
import code.art.drowningalert.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首先定义了一个继承自AsyncTask的MonitorTask，负责开启一个线程访问后台。然后在MonitorTask中定义了一个接口，MonitorListener，
 * 这个接口定义了一个抽象的方法onDanger()，并在MonitorTask中开启了一个引用，然后在MonitorTask中的onPostExecute调用了这个方法。
 * PoolingService中实例化了一个MonitorTask，然后又实现并实例化了一个MonitorTask的内部接口(MoniotrListener)，实现了onDanger方法，onDanger方法
 * 主要是用于对UI（地图）的更新。PoolingService中也有一个自定义的内部类，PoolingBinder extends Binder，并定义了两个方法，分别用于开启前台服务（同时
 * 开启轮询线程）以及关闭前台服务。然后实例化了一个内部类，并把实例通过重写了onBind方法的方法传了出去。然后在activity中，就可以通过在onServiceConnected
 * 中获取到service传出来的binder，然后调用这个binder的方法。
 */

public class PollingService extends Service {


//    private MonitorTask monitorTask;
    private PollingBinder mBinder = new PollingBinder();
    private String account;
//    private MonitorTask.MonitorListener listener = new MonitorTask.MonitorListener(){
//        @Override
//        public void onDanger(JSONArray j){
//
//        }
//    };

    private Handler handler;



    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }
    public class  PollingBinder extends Binder{
        public void startPolling(){
            startForeground(1,getNotification());
//            if(monitorTask==null){
//                monitorTask = new MonitorTask(listener);
//                monitorTask.execute(account);
//
//            }
        }
        public void initHandler(Handler h){
            handler = h;
        }
        public void initAccount(String acc){
            account = acc;
        }

        public void stopPolling(){
            getNotificationManager().cancel(1);
        }
    }
    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(){
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.icon_buoy);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("正在监听...");
        builder.setAutoCancel(false);
        builder.setContentText("正在监听溺水情况");
        return builder.build();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(account!=null) Log.d("异常检测","onStartcommand中account"+account);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("account",account)
                            .build();
                    Request request = new Request.Builder().post(requestBody).url("http://120.77.212.58:8088/alert").build();
                    Response response = client.newCall(request).execute();
                    Message message = new Message();
                    message.obj = response;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int interval = 10;
        long triggerAtTime = SystemClock.elapsedRealtime()+interval;
        Intent i = new Intent(this,PollingService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

}

