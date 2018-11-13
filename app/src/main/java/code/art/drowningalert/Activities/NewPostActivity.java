package code.art.drowningalert.Activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import code.art.drowningalert.R;
import code.art.drowningalert.Utils.SharedPreferencesUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPostActivity extends AppCompatActivity {

    private EditText contentText;
    private Button postButton;
    private SharedPreferencesUtil spHelper;
    private static String POST_URL="http://120.77.212.58:3000/mobile/issue";

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Toast.makeText(NewPostActivity.this,"发表成功",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(NewPostActivity.this,"发表失败",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spHelper=new SharedPreferencesUtil(this,"setting");
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.new_post_toolbar);
        toolbar.setTitle("发帖");
        setSupportActionBar(toolbar);
        contentText =findViewById(R.id.input_post_content);
        postButton=findViewById(R.id.btn_post);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    Message message = new Message();
                    @Override
                    public void run() {
                        try{
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("content",contentText.getText().toString())
                                    .add("account",spHelper.getString("name"))
                                    .build();
                            Request request = new Request.Builder().url(POST_URL).post(requestBody).build();
                            Response response = okHttpClient.newCall(request).execute();
                            message.what = new JSONObject(response.body().string()).getInt("resultcode");

                        }catch (Exception e){
                            e.printStackTrace();
                            message.what=0;
                        }
                        handler.sendMessage(message);

                    }
                }).start();
                finish();
            }
        });
    }

}
