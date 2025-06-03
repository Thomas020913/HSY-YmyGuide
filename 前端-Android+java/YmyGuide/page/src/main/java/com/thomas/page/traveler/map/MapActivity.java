package com.thomas.page.traveler.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.thomas.page.R;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import android.app.ProgressDialog;
import com.amap.api.maps.model.BitmapDescriptorFactory;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean isFirstLocation = true;
    private EditText etStart, etEnd;
    private Button btnCurrentLocation, btnStartNavi;
    private LatLng startLatLng, endLatLng, currentLatLng;
    private PopupWindow popupWindow;
    private boolean selectingStart = false;
    private CardView cardWalkInfo;
    private TextView tvWalkTime, tvWalkDistance;
    private ProgressDialog progressDialog;

    // 圆明园坐标（大致中心点）
    private static final LatLng YUANMINGYUAN_LOCATION = new LatLng(40.007978, 116.316745);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_map);

        // 初始化地图
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        etStart = findViewById(R.id.et_start);
        etEnd = findViewById(R.id.et_end);
        btnCurrentLocation = findViewById(R.id.btn_current_location);
        btnStartNavi = findViewById(R.id.btn_start_navi);

        if (mapView != null) {
            aMap = mapView.getMap();
            setupMap();
            checkAndRequestPermissions();
        }

        // 景点列表按钮
        Button btnSpotList = findViewById(R.id.btn_spot_list);
        btnSpotList.setOnClickListener(v -> {
            startActivity(new Intent(this, SpotListActivity.class));
        });

        // 输入提示监听
        etStart.setOnFocusChangeListener((v, hasFocus) -> selectingStart = hasFocus);
        etEnd.setOnFocusChangeListener((v, hasFocus) -> selectingStart = !hasFocus);
        etStart.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etStart.hasFocus()) showInputTips(s.toString(), true);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        etEnd.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etEnd.hasFocus()) showInputTips(s.toString(), false);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 当前位置按钮
        btnCurrentLocation.setOnClickListener(v -> {
            if (currentLatLng != null) {
                startLatLng = currentLatLng;
                etStart.setText("我的位置");
            } else {
                Toast.makeText(this, "定位中，请稍候...", Toast.LENGTH_SHORT).show();
            }
        });

        // 开始导航按钮
        btnStartNavi.setOnClickListener(v -> {
            if (startLatLng == null) {
                Toast.makeText(this, "请选择起点", Toast.LENGTH_SHORT).show();
                return;
            }
            if (endLatLng == null) {
                Toast.makeText(this, "请选择终点", Toast.LENGTH_SHORT).show();
                return;
            }
            // 显示正在规划路线提示
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("正在规划路线...");
                progressDialog.setCancelable(false);
            }
            progressDialog.show();
            cardWalkInfo.setVisibility(View.GONE);
            try {
                searchWalkRoute(startLatLng, endLatLng);
            } catch (AMapException e) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                throw new RuntimeException(e);
            }
        });

        cardWalkInfo = findViewById(R.id.card_walk_info);
        tvWalkTime = findViewById(R.id.tv_walk_time);
        tvWalkDistance = findViewById(R.id.tv_walk_distance);
    }

    private void showInputTips(String keyword, boolean isStart) {
        if (keyword.isEmpty()) {
            if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
            return;
        }
        InputtipsQuery query = new InputtipsQuery(keyword, "北京");
        Inputtips inputtips = new Inputtips(this, query);
        inputtips.setInputtipsListener((list, code) -> {
            if (code == 1000 && list != null && !list.isEmpty()) {
                showPopupWindow(list, isStart);
            } else {
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
            }
        });
        inputtips.requestInputtipsAsyn();
    }

    private void showPopupWindow(List<Tip> tips, boolean isStart) {
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        View contentView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null);
        ListView listView = new ListView(this);
        List<String> names = new ArrayList<>();
        for (Tip tip : tips) names.add(tip.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Tip tip = tips.get(position);
            if (isStart) {
                etStart.setText(tip.getName());
                if (tip.getPoint() != null)
                    startLatLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
            } else {
                etEnd.setText(tip.getName());
                if (tip.getPoint() != null)
                    endLatLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
            }
            if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        });
        popupWindow = new PopupWindow(listView, etStart.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        EditText anchor = isStart ? etStart : etEnd;
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(anchor, 0, 0, Gravity.NO_GRAVITY);
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                break;
            }
        }

        if (allGranted) {
            setupLocation();
        }
    }

    private void setupMap() {
        if (aMap != null) {
            // 设置地图初始位置到圆明园
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    YUANMINGYUAN_LOCATION, // 目标位置
                    17, // 缩放级别
                    30, // 俯仰角度
                    0  // 旋转角度
            )));

            // 设置地图UI控件
            aMap.getUiSettings().setMyLocationButtonEnabled(true); // 显示定位按钮
            aMap.getUiSettings().setZoomControlsEnabled(true); // 显示缩放按钮
        }
    }

    private void setupLocation() {
        try {
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
            locationClient = new AMapLocationClient(getApplicationContext());

            // 设置定位参数
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setInterval(2000);
            option.setNeedAddress(true); // 返回地址信息
            locationClient.setLocationOption(option);

            // 设置定位回调
            locationClient.setLocationListener(location -> {
                if (location != null) {
                    if (location.getErrorCode() == 0) {
                        // 定位成功
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        currentLatLng = latLng;
                        if (isFirstLocation) {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            isFirstLocation = false;
                        }
                    } else {
                        // 定位失败
                        String error = "定位失败：" + location.getErrorInfo();
                        Toast.makeText(MapActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // 启动定位
            locationClient.startLocation();

            // 设置定位蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.interval(2000);
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
            myLocationStyle.showMyLocation(true);
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "定位初始化失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void searchWalkRoute(LatLng start, LatLng end) throws AMapException {
        aMap.clear();
        RouteSearch routeSearch = new RouteSearch(this);
        LatLonPoint from = new LatLonPoint(start.latitude, start.longitude);
        LatLonPoint to = new LatLonPoint(end.latitude, end.longitude);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(from, to);
        WalkRouteQuery query = new WalkRouteQuery(fromAndTo);
        routeSearch.calculateWalkRouteAsyn(query);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (errorCode == 1000 && result != null && result.getPaths() != null && !result.getPaths().isEmpty()) {
                    WalkPath path = result.getPaths().get(0);
                    List<LatLng> latLngs = new ArrayList<>();
                    for (com.amap.api.services.route.WalkStep step : path.getSteps()) {
                        for (LatLonPoint p : step.getPolyline()) {
                            latLngs.add(new LatLng(p.getLatitude(), p.getLongitude()));
                        }
                    }
                    aMap.addPolyline(new PolylineOptions().addAll(latLngs).color(Color.BLUE).width(10f));
                    // 添加起点和终点标记（蓝色和红色）
                    aMap.addMarker(new MarkerOptions().position(startLatLng).title("起点").snippet(etStart.getText().toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    aMap.addMarker(new MarkerOptions().position(endLatLng).title("终点").snippet(etEnd.getText().toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
                    // 显示路线信息卡片
                    int duration = (int) path.getDuration(); // 秒
                    float distance = path.getDistance() / 1000f; // 千米
                    tvWalkTime.setText("用时：" + formatDuration(duration));
                    tvWalkDistance.setText(String.format("距离：%.2f公里", distance));
                    cardWalkInfo.setVisibility(View.VISIBLE);
                    Toast.makeText(MapActivity.this, "路线规划成功！", Toast.LENGTH_SHORT).show();
                } else {
                    cardWalkInfo.setVisibility(View.GONE);
                    Toast.makeText(MapActivity.this, "未找到步行路线", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onBusRouteSearched(com.amap.api.services.route.BusRouteResult busRouteResult, int i) {}
            @Override public void onDriveRouteSearched(com.amap.api.services.route.DriveRouteResult driveRouteResult, int i) {}
            @Override public void onRideRouteSearched(com.amap.api.services.route.RideRouteResult rideRouteResult, int i) {}
        });
    }

    // 辅助方法：格式化时长
    private String formatDuration(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        if (min > 0) {
            return min + "分" + (sec > 0 ? sec + "秒" : "");
        } else {
            return sec + "秒";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                setupLocation();
            } else {
                Toast.makeText(this, "需要定位权限才能显示当前位置", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}