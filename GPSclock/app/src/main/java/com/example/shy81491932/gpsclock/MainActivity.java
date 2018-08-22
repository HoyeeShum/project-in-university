package com.example.shy81491932.gpsclock;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;


public class MainActivity extends AppCompatActivity implements LocationSource,AMapLocationListener{
    private MapView mapView = null;
    private AMap aMap;
    //location
    private AMapLocationClient aMapLocationClient = null;
    private AMapLocationClientOption aMapLocationClientOption = null;
    private LocationSource.OnLocationChangedListener listener = null;
    MyLocationStyle style;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private boolean isSet = false;
    //distance
    private double dis = 0;
    private float realDis = 0;
    private MediaPlayer mediaPlayer01;
    private Vibrator vb;
    private AMapLocation amapLo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Button tbutton = findViewById(R.id.toast);
        tbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击屏幕右下方按钮添加闹钟", Toast.LENGTH_SHORT).show();
            }
        });


        //getMap
        mapView = findViewById(R.id.map);
        //createMap
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        //setLocationButton
        UiSettings uiSettings = aMap.getUiSettings();
        //定位监听
        aMap.setLocationSource(this);
        //显示定位按钮
        uiSettings.setMyLocationButtonEnabled(true);
        //是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
        style = new MyLocationStyle();
        style.interval(2000);
        aMap.setMyLocationStyle(style);
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        style.showMyLocation(true);
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);

        //startLocating
        initLoc();
        amapLo = aMapLocationClient.getLastKnownLocation();
    }



    private MarkerOptions getMarkerOptions(LatLng point ) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        //位置
        options.position(new LatLng(point.latitude, point.longitude));
        options.period(60);
        options.snippet("目的地");
        return options;
    }
    //更新目的地
    private Marker marker;
    private void updateMarker(LatLng point){
        if(marker==null){
            marker = aMap.addMarker(getMarkerOptions(point));
        }
        else {
            marker.setVisible(false);
            marker = aMap.addMarker(getMarkerOptions(point));

        }
    }
    //dialog
    private void dialog(double a){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog);
        builder.setTitle(getString(R.string.dialogtit));
        builder.setMessage("距离目的地还有"+a+"m");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(MainActivity.this,"请点击左下方按钮结束",Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }


        //定位
        private void initLoc() {
            //初始化定位
            aMapLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            aMapLocationClient.setLocationListener(this);
            //初始化定位参数
            aMapLocationClientOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            aMapLocationClientOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            aMapLocationClientOption.setOnceLocation(false);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            aMapLocationClientOption.setMockEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            aMapLocationClientOption.setInterval(2000);
            //给定位客户端对象设置定位参数
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            //启动定位
            aMapLocationClient.startLocation();
//            amapLo = aMapLocationClient.getLastKnownLocation();
        }

    //定位回调函数
        @Override
        public void onLocationChanged(final AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码


                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    listener.onLocationChanged(amapLo);
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_SHORT).show();
                    isFirstLoc = false;


                    //set distance
                    FloatingActionButton button = findViewById(R.id.button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent =  new Intent(MainActivity.this,AddClockActivity.class);
                            startActivityForResult(intent,1);
                            aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    double latitude = latLng.latitude;
                                    double longitude = latLng.longitude;

                                    //get mapDis
                                    LatLng point = new LatLng(latitude,longitude);
                                    LatLng point1 = new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                                    updateMarker(point);
                                    realDis = AMapUtils.calculateLineDistance(point,point1);
                                    if(realDis<=dis){
                                        //setClockMusic
                                        mediaPlayer01 = MediaPlayer.create(getBaseContext(),R.raw.ring);
                                        mediaPlayer01.start();
                                        //setVibration
                                        vb = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                                        long []pattern = {400,500,400,500};
                                        vb.vibrate(pattern,0);
                                        dialog(dis);
                                    }
                        }
                    });


                            Button bc = findViewById(R.id.checkButton);
                            bc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(MainActivity.this,"距离目的地还有"+realDis+"m\n设置距离为"+dis,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    FloatingActionButton b2 = findViewById(R.id.closeButton);
                    b2.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            isSet = false;
                            if(vb!=null) {
                                mediaPlayer01.release();
                                vb.cancel();
                            }
                            marker.setVisible(false);
                        }
                    });



                }
            }
        }
        else{
                Toast.makeText(getApplicationContext(),"定位失败",Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
           switch (requestCode){
               case 1:
                   if(resultCode == RESULT_OK){
                       String di = data.getStringExtra("distance");
                       dis = Integer.parseInt(di);
                   }
           }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener1){
        listener = listener1;
    }

    @Override
    public void deactivate(){
        listener = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //destroyMap
        mapView.onDestroy();
        if(null != aMapLocationClient){
            aMapLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}


