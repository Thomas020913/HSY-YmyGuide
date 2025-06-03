package com.thomas.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.FrameLayout;
import android.view.Gravity;

import com.thomas.page.admin.ProductActivity;
import com.thomas.page.admin.ScenicSpotActivity;
import com.thomas.page.admin.TicketActivity;
import com.thomas.page.admin.UserActivity;
import com.thomas.page.traveler.ImageSliderAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // 轮播图初始化
        viewPager = findViewById(R.id.viewPager);
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.ymy1);
        imageList.add(R.drawable.ymy2);
        imageList.add(R.drawable.ymy3);
        imageList.add(R.drawable.ymy4);

        adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);
        // 设置欢迎语内容
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText("欢迎来到圆明园导览系统——\n管理员全局管理页面");

        // 初始化底部导航
        setupBottomNavigation();
    }
    private void setupBottomNavigation() {
        TextView ticketBtn = findViewById(R.id.btn_ticket);
        TextView userBtn = findViewById(R.id.btn_user);
        TextView shopBtn = findViewById(R.id.btn_shop);
        TextView profileBtn = findViewById(R.id.btn_profile);

        ticketBtn.setOnClickListener(v -> startActivity(new Intent(this, TicketActivity.class)));
        userBtn.setOnClickListener(v -> startActivity(new Intent(this, UserActivity.class)));
        shopBtn.setOnClickListener(v -> startActivity(new Intent(this, ProductActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ScenicSpotActivity.class)));
    }
}
