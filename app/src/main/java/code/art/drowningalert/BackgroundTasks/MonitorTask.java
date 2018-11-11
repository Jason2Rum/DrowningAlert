package code.art.drowningalert.BackgroundTasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import code.art.drowningalert.Service.PollingService;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.ALARM_SERVICE;

public class MonitorTask extends AsyncTask<String,Integer,JSONArray> {
    private MonitorListener monitorListener;
    public MonitorTask(MonitorListener monitorListener){
        this.monitorListener = monitorListener;
    }

    public interface MonitorListener{
        void onDanger(JSONArray j);
//        void onSafe(Map map);
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        try{
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("account",params[0])
                    .build();
            Request request = new Request.Builder().post(requestBody).url("http://120.77.212.58:8088/alert").build();
            Response response = client.newCall(request).execute();

            //TODO:提取经纬度返回结果
            return null;
        }catch (IOException e){
            return null;
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray j) {
        if(j==null)
            return;
        monitorListener.onDanger(j);

    }
}
