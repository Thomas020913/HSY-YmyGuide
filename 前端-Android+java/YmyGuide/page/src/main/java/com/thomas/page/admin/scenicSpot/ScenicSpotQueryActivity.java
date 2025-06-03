package com.thomas.page.admin.scenicSpot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScenicSpotQueryActivity extends AppCompatActivity {
    private RecyclerView rvSpotList;
    private SpotAdapter adapter;
    private final List<Spot> spotList = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_spot_list);
        rvSpotList = findViewById(R.id.rv_spot_list);
        rvSpotList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SpotAdapter(spotList);
        rvSpotList.setAdapter(adapter);
        fetchSpotList();
    }

    private void fetchSpotList() {
        new Thread(() -> {
            try {
                String response = HttpRequestUtil.postRequest("http://192.168.166.112:8080/api/scenic/list", "{}");
                Type listType = new TypeToken<List<Spot>>(){}.getType();
                List<Spot> spots = new Gson().fromJson(response, listType);
                mainHandler.post(() -> {
                    spotList.clear();
                    if (spots != null) spotList.addAll(spots);
                    adapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // 景点数据模型
    static class Spot {
        String name;
        String intro;
        String imageUrl;
    }

    // RecyclerView适配器
    static class SpotAdapter extends RecyclerView.Adapter<SpotViewHolder> {
        private final List<Spot> data;
        SpotAdapter(List<Spot> data) { this.data = data; }
        @NonNull
        @Override
        public SpotViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spot, parent, false);
            return new SpotViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
            Spot spot = data.get(position);
            holder.bind(spot);
        }
        @Override
        public int getItemCount() { return data.size(); }
    }

    // ViewHolder
    static class SpotViewHolder extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvName;
        private final android.widget.TextView tvDesc;
        private final android.widget.ImageView ivSpot;
        SpotViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_spot_name);
            tvDesc = itemView.findViewById(R.id.tv_spot_desc);
            ivSpot = itemView.findViewById(R.id.iv_spot);
        }
        void bind(Spot spot) {
            tvName.setText(spot.name);
            tvDesc.setText(spot.intro);
            com.bumptech.glide.Glide.with(itemView.getContext())
                    .load(spot.imageUrl)
                    .into(ivSpot);
        }
    }
} 