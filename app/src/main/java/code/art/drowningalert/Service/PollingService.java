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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import code.art.drowningalert.Activities.MainActivity;
import code.art.drowningalert.Item.AlertLoc;
import code.art.drowningalert.R;
import code.art.drowningalert.Utils.LocationParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    private final String POLLING_URL="http://120.77.212.58:3000/mobile/alert";
    public static final int DANGER_FLAG =120;

    private PollingBinder mBinder = new PollingBinder();
    private String region;

    private Handler handler;



    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }
    public class  PollingBinder extends Binder{
        public void setupForeground(){
            startForeground(1,getNotification());

        }
        public void initHandler(Handler h){
            handler = h;
        }
        public void initRegion(String reg){
            region = reg;
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
        builder.setSmallIcon(R.mipmap.icon_buoy);
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
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().get().url(POLLING_URL+"?region="+region).build();
                    Response response = client.newCall(request).execute();
                    JSONObject result = new JSONObject( response.body().string());
                    List<AlertLoc> alertLocs = new ArrayList<>();

                    Message message = new Message();
                    if(result.getInt("resultcode")==1){

                        JSONArray locs = result.getJSONArray("data");

                        for(int i=0;i<locs.length();i++){
                            AlertLoc alertLoc= new AlertLoc();
                            alertLoc.setUid(locs.getJSONObject(i).getString("uid"));
                            alertLoc.setLatitude(LocationParser.parse(locs.getJSONObject(i).getString("latitude"),LocationParser.LATITUDE));
                            alertLoc.setLongitude(LocationParser.parse(locs.getJSONObject(i).getString("longitude"),LocationParser.LONGITUDE));
                            alertLoc.setTag(locs.getJSONObject(i).getInt("tag"));

                            if(alertLoc.getTag()==1)message.what=DANGER_FLAG;
                            alertLocs.add(alertLoc);
                        }
                    }

                    message.obj = alertLocs;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int interval = 2;
        long triggerAtTime = SystemClock.elapsedRealtime()+interval;
        Intent i = new Intent(this,PollingService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

}

