package com.thomas.page.traveler;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thomas.page.R;
import com.thomas.page.traveler.ticket.TicketAdapter;
import com.thomas.page.traveler.ticket.TicketInventory;
import com.thomas.page.util.HttpRequestUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class TicketActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<TicketInventory> ticketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_ticket_query);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchTicketInventory();
    }

    private void fetchTicketInventory() {
        new Thread(() -> {
            try {
                String response = HttpRequestUtil.getRequest("http://192.168.166.112:8080/api/ticket/list");
                if (response != null) {
                    JSONArray jsonArray = new JSONArray(response);

                    // 临时 map 存放合并后的数据
                    Map<String, Integer> stockMap = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String ticketName = obj.getString("ticketName");
                        int currentStock = obj.getInt("currentStock");

                        // 合并库存
                        stockMap.put(ticketName, stockMap.getOrDefault(ticketName, 0) + currentStock);
                    }

                    // 清空旧数据
                    ticketList.clear();
                    for (Map.Entry<String, Integer> entry : stockMap.entrySet()) {
                        TicketInventory ti = new TicketInventory();
                        ti.setTicketName(entry.getKey());
                        ti.setCurrentStock(entry.getValue());
                        ticketList.add(ti);
                    }

                    runOnUiThread(() -> {
                        TicketAdapter adapter = new TicketAdapter(this, ticketList);
                        recyclerView.setAdapter(adapter);
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "未获取到数据", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
