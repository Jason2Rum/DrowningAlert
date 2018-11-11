package code.art.drowningalert.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import code.art.drowningalert.R;
import code.art.drowningalert.widgets.LoadingDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgetPwdActivity extends AppCompatActivity {

    private final String QUESTION_URL ="";
    private final String PASSWORD_URL ="";


    private TextView scrQuestionText;
    private EditText scrAnswerText;
    private Button accountButton;
    private Button passwordButton;
    private LoadingDialog loadingDialog;
    private EditText accountText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        initViews();
        initEvent();


    }
    private void initViews(){
        scrAnswerText=findViewById(R.id.et_answer);
        scrQuestionText = findViewById(R.id.tv_question);
        accountButton = findViewById(R.id.bt_verify);
        accountText=findViewById(R.id.et_forget_account);
        passwordButton = findViewById(R.id.btn_login);
    }
    private void initEvent(){
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountText.getText().toString().equals("")){
                    Toast.makeText(ForgetPwdActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                }else{
                    AnswerTask questionTask = new AnswerTask();
                    questionTask.execute(accountText.getText().toString(),scrAnswerText.getText().toString());
                }
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountText.getText().toString().equals(""))
                    Toast.makeText(ForgetPwdActivity.this,"请输入账号",Toast.LENGTH_SHORT).show();
                else{
                    AccountTask accountTask = new AccountTask();
                    accountTask.execute(accountText.getText().toString());
                }
            }
        });




    }

    private class AnswerTask extends AsyncTask<String,Intent,String>{
        @Override
        protected void onPreExecute(){
            loadingDialog = new LoadingDialog(ForgetPwdActivity.this,"验证中...",false);
            loadingDialog.show();
        }
        @Override
        protected String doInBackground(String...params){
            try{
                String account= params[0];
                String answer = params[1];
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("account",account)
                        .add("answer",answer)
                        .build();
                Request request = new Request.Builder().post(requestBody).url(PASSWORD_URL).build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (IOException e){
                e.printStackTrace();
                return "";
            }

        }
        @Override
        protected void onPostExecute(String password){
            loadingDialog.dismiss();
            if(password.equals(""))
                Toast.makeText(ForgetPwdActivity.this,"获取密码失败",Toast.LENGTH_LONG).show();
            else
            Toast.makeText(ForgetPwdActivity.this,"您的密码为:"+password,Toast.LENGTH_LONG).show();
        }
    }

    private class AccountTask extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute(){
            loadingDialog = new LoadingDialog(ForgetPwdActivity.this,"获取密保问题",false);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String...params){
            try{
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("account",params[0])
                        .build();
                Request request = new Request.Builder().post(requestBody).url(QUESTION_URL).build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (IOException e){
                e.printStackTrace();
                return "";
            }
        }
        @Override
        protected void onPostExecute(String result){
            loadingDialog.dismiss();
            if(result.equals("")){
                Toast.makeText(ForgetPwdActivity.this,"您的账号不存在",Toast.LENGTH_SHORT).show();
            }else{
                scrQuestionText.setText(result);
            }
        }
    }



}
