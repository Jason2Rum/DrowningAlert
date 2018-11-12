package code.art.drowningalert.Fragments;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

import code.art.drowningalert.Item.AlertLoc;
import code.art.drowningalert.MyOrientationListener;
import code.art.drowningalert.R;

public class LocationFragment extends Fragment {
    public LocationClient mLocationClient;
    private LocationClientOption option;
    private int isFirstLocate = 0;
    private BaiduMap baiduMap;
    private MapView mapView;
    private LatLng myPos;
    private MyOrientationListener myOrientationListener;
    private BitmapDescriptor bitmapDescriptor;
    private float mLastX;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_location,container,false);


        mLocationClient = new LocationClient(getContext());
        option = new LocationClientOption();
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyLocationListener());

        mapView = (MapView)view.findViewById(R.id.bmapView);
        baiduMap=mapView.getMap();

        initMyLoc();

        baiduMap.setMyLocationEnabled(true);
        initLocationClientOption(option);
        initEvents();
        mLocationClient.start();
        return view;


    }


    public void initEvents(){
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng alertPos = marker.getPosition();

                Button button = new Button(getContext());
                button.setBackgroundResource(R.drawable.popup);
                InfoWindow mInfoWindow = new InfoWindow(button, alertPos, -47);
                button.setText((String.valueOf(DistanceUtil. getDistance(myPos, alertPos))).substring(0,6)+"米");
                button.setTextColor(0xffff00ff);
                baiduMap.showInfoWindow(mInfoWindow);
                return false;
            }
        });
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){//接收到位置
            if(location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType()== BDLocation.TypeNetWorkLocation){
                navigateTo(location);
            }
        }
    }



    //移动到我的位置，并让“我”显示在地图上
    private void navigateTo(BDLocation location){


        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
        myPos = ll;//获取我的位置坐标

        if(isFirstLocate<3){
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.newLatLngZoom(ll,18f);
            baiduMap.animateMapStatus(update);
            isFirstLocate ++;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.accuracy(location.getRadius());
        locationBuilder.direction(mLastX);
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
        baiduMap.setMyLocationConfigeration(configuration);

    }

    public void setAlertMarker(List<AlertLoc> locs){
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_mark);
        List<LatLng> locList = new ArrayList<>();
        try{
            for(AlertLoc alertLoc:locs){

                locList.add(new LatLng(alertLoc.getLatitude(),alertLoc.getLongitude()));


            }
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<locList.size();i++){
            OverlayOptions ele =  new MarkerOptions().position(locList.get(i)).icon(bitmap);

            options.add(ele);

        }
        baiduMap.addOverlays(options);
    }

    private void initMyLoc() {
        //初始化图标
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.arrow);
        //方向传感器监听
        myOrientationListener = new MyOrientationListener(getContext());

        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

                    @Override
                    public void onOrientationChanged(float x) {
                        mLastX = x;
                    }
                });
    }


    private void initLocationClientOption(LocationClientOption option){
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setScanSpan(1001);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

    }

    @Override
    public void onDestroy(){

        mLocationClient.stop();
        Log.d("碎片","onDestroy");
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("碎片","onResume");
        mapView.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("碎片","onPause");
        myOrientationListener.stop();
        mapView.onPause();
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("碎片","onStart");
        myOrientationListener.start();
    }

}
