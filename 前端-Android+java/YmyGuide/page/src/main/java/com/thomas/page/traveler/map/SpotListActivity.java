package com.thomas.page.traveler.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.widget.*;
import android.view.*;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SpotListActivity extends AppCompatActivity {
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
        String audioUrl;
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
        private final TextView tvName;
        private final TextView tvDesc;
        private final ImageView ivSpot;
        private final ImageButton btnPlay;

        private MediaPlayer mediaPlayer;

        SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_spot_name);
            tvDesc = itemView.findViewById(R.id.tv_spot_desc);
            ivSpot = itemView.findViewById(R.id.iv_spot);
            btnPlay = itemView.findViewById(R.id.btn_play_audio);
        }

        void bind(Spot spot) {
            tvName.setText(spot.name);
            tvDesc.setText(spot.intro);
            Glide.with(itemView.getContext())
                    .load(spot.imageUrl)
                    .into(ivSpot);

            btnPlay.setOnClickListener(v -> {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource("http://192.168.166.112:8080/audios/" + spot.audioUrl + ".m4a");
                        mediaPlayer.setOnPreparedListener(mp -> {
                            mp.start();
                            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        });
                        mediaPlayer.setOnCompletionListener(mp -> {
                            btnPlay.setImageResource(android.R.drawable.ic_media_play);
                            mp.release();
                            mediaPlayer = null;
                        });
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(itemView.getContext(), "播放失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
} 