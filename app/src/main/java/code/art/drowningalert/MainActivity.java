package code.art.drowningalert;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import code.art.drowningalert.Service.PollingService;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bottomNavigationBar;
    private int currentTabIndex=1;
    public LocationFragment locationFragment;
    private String account;
    private Intent mIntent;



    List<String> permissionList = new ArrayList<>();

    private PollingService.PollingBinder pollingBinder; //一个操纵service的遥控器，在ServiceConnection中的onServiceConnected中实例化

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pollingBinder = (PollingService.PollingBinder) service;
            pollingBinder.startPolling();
            if(account!=null)Log.d("空检测",account);
            pollingBinder.initAccount(account);
            if(handler!=null)
            Log.d("空检测","handler不空");
            pollingBinder.initHandler(handler);
            startService(mIntent);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            try{
                if(locationFragment!=null){
                    locationFragment.setAlertMarker(new JSONArray(msg.obj.toString()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        getPermissions();
        setContentView(R.layout.activity_main);
        Intent callerIntent = getIntent();
        account= callerIntent.getStringExtra("account");//获取上一个活动传来的账户
        Log.d("异常检查","login传给mainactivity的account为"+account);
        initViews();
        initEvents();
         locationFragment = new LocationFragment();
        replaceFragment(locationFragment);
//        startPollingService();

        mIntent = new Intent(this, PollingService.class);
        bindService(mIntent,connection,BIND_AUTO_CREATE);


    }



    private void getPermissions(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }
    }


    private void initViews() {
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setActiveColor(R.color.dodgerblue)
                .addItem(new BottomNavigationItem(R.drawable.icon_home_click,"首页")
                        .setInactiveIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_home)))
        .addItem(new BottomNavigationItem(R.drawable.icon_location_click,"监察")
                .setInactiveIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_location)))
        .addItem(new BottomNavigationItem(R.drawable.icon_zone_click,"圈子")
                .setInactiveIcon(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_zone)));
        bottomNavigationBar.setFirstSelectedPosition(1).initialise();
    }

    private void initEvents(){
        bottomNavigationBar //设置lab点击事件
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(int position) {

                        if(currentTabIndex!=position){
                            switch (position){
                                case 0:
                                    if(pollingBinder==null){
                                        Log.d("binder","空的");
                                    }
                                    replaceFragment(new HomeFragment());
                                    break;
                                case 1:
                                     locationFragment = new LocationFragment();
                                    replaceFragment(locationFragment);

                                    break;
                                case 2:
                                    replaceFragment(new ZoneFragment());
                                    break;
                                default:
                                    break;
                            }
                            currentTabIndex = position;
                        }

                    }

                    @Override
                    public void onTabUnselected(int position) {
                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frg_space,fragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults){
        switch(requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }

                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(connection);
    }


}
