package code.art.drowningalert.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import code.art.drowningalert.R;
import code.art.drowningalert.SignUpInfo;
import code.art.drowningalert.Utils.SharedPreferencesUtil;
import code.art.drowningalert.widgets.LoadingDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    //布局内的控件
    private EditText accountText;
    private EditText passwordText;
    private Button loginButton;
    private CheckBox passwordCheckBox;
    private CheckBox loginCheckBox;
    private ImageView seePasswordImage;
    private TextView signUpText;
    private TextView forgetPwdText;
    private final String LOGIN_URL ="http://120.77.212.58:3000/mobile/login";

    public static final int SIGN_UP_REQ_CODE = 1;
    public static final int LOGIN_SUCCESS=1;
    public static final int LOGIN_FAIL=2;

    private LoadingDialog mLoadingDialog; //显示正在加载的对话框

    private Handler handler =new Handler(){
        public void handleMessage(Message msg){
            Map params = (Map)msg.obj;
            Log.d("测试","handler");
            switch (msg.what){
                case LOGIN_SUCCESS:
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("account",accountText.getText().toString());
                    intent.putExtra("password",passwordText.getText().toString());
                    intent.putExtra("nickname",params.get("nickname").toString());
                    intent.putExtra("profileUrl",params.get("profileUrl").toString());
                    intent.putExtra("region",params.get("region").toString());
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_FAIL:
                    showToast("账号或密码错误");
            }
        }
    };
    public void showToast(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setupEvents();
        initData();

    }



    private void initData() {


        //判断用户第一次登陆
        if (firstLogin()) {
            passwordCheckBox.setChecked(false);//取消记住密码的复选框
            loginCheckBox.setChecked(false);//取消自动登录的复选框
        }
        //判断是否记住密码
        if (rememberPassword()) {
            passwordCheckBox.setChecked(true);//勾选记住密码
            setTextNameAndPassword();//把密码和账号输入到输入框中
        } else {
            setTextName();//把用户账号放到输入账号的输入框中
        }

        //判断是否自动登录
        if (autoLogin()) {
            loginCheckBox.setChecked(true);
            login();//去登录就可以

        }
    }

    /**
     * 把本地保存的数据设置数据到输入框中
     */
    public void setTextNameAndPassword() {
        accountText.setText("" + getLocalName());
        passwordText.setText("" + getLocalPassword());
    }

    /**
     * 设置数据到输入框中
     */
    public void setTextName() {
        accountText.setText("" + getLocalName());
    }


    /**
     * 获得保存在本地的用户名
     */
    public String getLocalName() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
        String name = helper.getString("name");
        return name;
    }


    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
        String password = helper.getString("password");
        return password;


    }

    /**
     * 判断是否自动登录
     */
    private boolean autoLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
        boolean autoLogin = helper.getBoolean("autoLogin", false);
        return autoLogin;
    }

    /**
     * 判断是否记住密码
     */
    private boolean rememberPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
        boolean rememberPwd = helper.getBoolean("rememberPassword", false);
        return rememberPwd;
    }


    private void initViews() {
        loginButton = (Button) findViewById(R.id.btn_login);
        accountText = (EditText) findViewById(R.id.text_account);
        passwordText = (EditText) findViewById(R.id.text_pwd);
        passwordCheckBox = (CheckBox) findViewById(R.id.checkBox_pwd);
        loginCheckBox = (CheckBox) findViewById(R.id.checkBox_auto_login);
        seePasswordImage = (ImageView) findViewById(R.id.hide_pwd_image);
        signUpText = findViewById(R.id.sign_up_entrance);
        forgetPwdText = findViewById(R.id.forget_pwd_text);
    }

    private void setupEvents() {
        loginButton.setOnClickListener(this);
        passwordCheckBox.setOnCheckedChangeListener(this);
        loginCheckBox.setOnCheckedChangeListener(this);
        seePasswordImage.setOnClickListener(this);
        signUpText.setOnClickListener(this);
        forgetPwdText.setOnClickListener(this);

    }

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
        boolean first = helper.getBoolean("first", true);
        if (first) {
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并创建记住密码和自动登录是默认不选，创建账号和密码为空
            helper.putValues(new SharedPreferencesUtil.ContentValue("first", false),
                    new SharedPreferencesUtil.ContentValue("rememberPassword", false),
                    new SharedPreferencesUtil.ContentValue("autoLogin", false),
                    new SharedPreferencesUtil.ContentValue("name", ""),
                    new SharedPreferencesUtil.ContentValue("password", ""));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case SIGN_UP_REQ_CODE:
                if(resultCode==RESULT_OK){
                    accountText.setText(data.getStringExtra("account"));
                    passwordText.setText(data.getStringExtra("password"));

                }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                saveAccount();    //无论如何保存一下用户名
                if(rememberPassword()){
                    SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
                    helper.putValues(new SharedPreferencesUtil.ContentValue("password",getPassword()));
                }
                login(); //登陆
                break;
            case R.id.hide_pwd_image:
                setPasswordVisibility();    //改变图片并设置输入框的文本可见或不可见
                break;
            case R.id.sign_up_entrance:
                startActivityForResult(new Intent(LoginActivity.this,SignUpActivity.class), SIGN_UP_REQ_CODE);

                break;
            case R.id.forget_pwd_text:
                startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));

                break;
            default:
                break;
        }
    }

    /**
     *
     */
    private void login() {

        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (getAccount().isEmpty()){
            showToast("你输入的账号为空！");
            return;
        }

        if (getPassword().isEmpty()){
            showToast("你输入的密码为空！");
            return;
        }
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        verifyAccount(accountText.getText().toString(),passwordText.getText().toString());
        hideLoading();

    }
    public void verifyAccount(final String account, final String pwd){
        setLoginBtnClickable(false);//点击登录后，设置登录按钮不可点击状态
        Log.d("测试","account:"+account);
        Log.d("测试","pwd"+pwd);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d("测试","线程");
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody user = new FormBody.Builder()
                            .add("account",account)
                            .add("password",pwd)
                            .build();

                    Request request = new Request.Builder().url(LOGIN_URL).post(user).build();
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject result = new JSONObject(response.body().string());

                    Message msg =new Message();
                    if(result.getInt("resultcode")==1){
                        result = result.getJSONArray("data").getJSONObject(0);
                        msg.what=LOGIN_SUCCESS;
                        Map<String,String> userInfo =new HashMap<>();
                        userInfo.put("nickname",result.getString("nickname"));
                        userInfo.put("region",result.getString("region"));
                        userInfo.put("profileUrl",result.getString("profileUrl"));
                        msg.obj = userInfo;
                    }else {
                        msg.what=LOGIN_FAIL;
                    }
                        handler.sendMessage(msg);

                    Log.d("测试","执行到这一步");
                    setLoginBtnClickable(true);  //这里解放登录按钮，设置为可以点击

                }catch (Exception e){
                    Log.d("测试","线程异常");
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 保存用户账号
     */
    public void saveAccount() {
        if (!getAccount().equals("") || !getAccount().equals("请输入登录账号")) {
            SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");
            helper.putValues(new SharedPreferencesUtil.ContentValue("name", getAccount()));
        }

    }

    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (seePasswordImage.isSelected()) {
            seePasswordImage.setSelected(false);
            //密码不可见
            passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            seePasswordImage.setSelected(true);
            //密码可见
            passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    /**
     * 获取账号
     */
    public String getAccount() {
        return accountText.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return passwordText.getText().toString().trim();//去掉空格
    }


    /**
     * 保存用户选择“记住密码”和“自动登陆”的状态
     */
    private void saveCheckBoxState() {
        saveCheckBoxState(passwordCheckBox, loginCheckBox);
    }

    /**
     * 保存按钮的状态值
     */
    public void saveCheckBoxState(CheckBox checkBox_password, CheckBox checkBox_login) {

        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtil helper = new SharedPreferencesUtil(this, "setting");

        //如果设置自动登录
        if (checkBox_login.isChecked()) {
            //创建记住密码和自动登录是都选择,保存密码数据
            helper.putValues(
                    new SharedPreferencesUtil.ContentValue("rememberPassword", true),
                    new SharedPreferencesUtil.ContentValue("autoLogin", true),
                    new SharedPreferencesUtil.ContentValue("password", getPassword()));

        } else if (!checkBox_password.isChecked()) { //如果没有保存密码，那么自动登录也是不选的
            //创建记住密码和自动登录是默认不选,密码为空
            helper.putValues(
                    new SharedPreferencesUtil.ContentValue("rememberPassword", false),
                    new SharedPreferencesUtil.ContentValue("autoLogin", false),
                    new SharedPreferencesUtil.ContentValue("password", ""));
        } else if (checkBox_password.isChecked()) {   //如果保存密码，没有自动登录
            //创建记住密码为选中和自动登录是默认不选,保存密码数据
            helper.putValues(
                    new SharedPreferencesUtil.ContentValue("rememberPassword", true),
                    new SharedPreferencesUtil.ContentValue("autoLogin", false),
                    new SharedPreferencesUtil.ContentValue("password", getPassword()));
        }
    }

    /**
     * 是否可以点击登录按钮
     *
     * @param clickable
     */
    public void setLoginBtnClickable(boolean clickable) {
        loginButton.setClickable(clickable);
    }


    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, "loading...", false);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }
    }


    /**
     * CheckBox点击时的回调方法 ,不管是勾选还是取消勾选都会得到回调
     *
     * @param buttonView 按钮对象
     * @param isChecked  按钮的状态
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == passwordCheckBox) {  //记住密码选框发生改变时
            if (!isChecked) {   //如果取消“记住密码”，那么同样取消自动登陆
                loginCheckBox.setChecked(false);
            }
        } else if (buttonView == loginCheckBox) {   //自动登陆选框发生改变时
            if (isChecked) {   //如果选择“自动登录”，那么同样选中“记住密码”
                passwordCheckBox.setChecked(true);
            }
        }
    }


    /**
     * 监听回退键
     */
    @Override
    public void onBackPressed() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.cancel();
            } else {
                finish();
            }
        } else {
            finish();
        }

    }

    /**
     * 页面销毁前回调的方法
     */
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }
}
