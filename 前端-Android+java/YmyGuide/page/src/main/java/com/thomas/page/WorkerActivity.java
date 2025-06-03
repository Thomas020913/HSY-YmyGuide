package com.thomas.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.FrameLayout;
import android.view.Gravity;

import com.thomas.page.traveler.ImageSliderAdapter;
import com.thomas.page.worker.ProductActivity;
import com.thomas.page.worker.TicketActivity;

import java.util.ArrayList;
import java.util.List;


public class WorkerActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
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
        tvWelcome.setText("欢迎来到圆明园导览系统——\n园区工作人员管理页面");

        // 初始化底部导航
        setupBottomNavigation();
    }
    private void setupBottomNavigation() {
        TextView ticketBtn = findViewById(R.id.btn_ticket);
        TextView shopBtn = findViewById(R.id.btn_shop);

        ticketBtn.setOnClickListener(v -> startActivity(new Intent(this, TicketActivity.class)));
        shopBtn.setOnClickListener(v -> startActivity(new Intent(this, ProductActivity.class)));

    }
}
