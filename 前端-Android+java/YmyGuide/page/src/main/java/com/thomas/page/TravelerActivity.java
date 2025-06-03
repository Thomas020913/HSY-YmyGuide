package com.thomas.page;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.TextView;

import com.thomas.page.traveler.ImageSliderAdapter;
import com.thomas.page.traveler.map.MapActivity;
import com.thomas.page.traveler.ProductActivity;
import com.thomas.page.traveler.TicketActivity;

import java.util.ArrayList;
import java.util.List;

public class TravelerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler);

        // 轮播图初始化
        viewPager = findViewById(R.id.viewPager);
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.ymy1);
        imageList.add(R.drawable.ymy2);
        imageList.add(R.drawable.ymy3);
        imageList.add(R.drawable.ymy4);

        adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);

        // 初始化底部导航
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        TextView ticketBtn = findViewById(R.id.btn_ticket);
        TextView mapBtn = findViewById(R.id.btn_map);
        TextView shopBtn = findViewById(R.id.btn_shop);

        ticketBtn.setOnClickListener(v -> startActivity(new Intent(this, TicketActivity.class)));
        mapBtn.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        shopBtn.setOnClickListener(v -> startActivity(new Intent(this, ProductActivity.class)));
    }
}
