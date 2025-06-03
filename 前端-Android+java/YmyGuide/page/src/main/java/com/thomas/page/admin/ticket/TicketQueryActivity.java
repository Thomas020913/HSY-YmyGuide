package com.thomas.page.admin.ticket;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TicketQueryActivity extends AppCompatActivity {
    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ticket_query);
        table = findViewById(R.id.ticket_table);
        // 1. 发起网络请求
        fetchTicketList();
    }

    private void fetchTicketList() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.166.112:8080/api/ticket/list";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String jsonStr = response.body().string();
                    runOnUiThread(() -> fillTable(jsonStr));
                }
            }
        });
    }

    private void fillTable(String jsonStr) {
        try {
            JSONArray arr = new JSONArray(jsonStr);
            int childCount = table.getChildCount();
            while (childCount > 1) {
                table.removeViewAt(1);
                childCount--;
            }
            for (int i = 0; i < arr.length(); i++) {
                try {
                    JSONObject obj = arr.getJSONObject(i);
                    TableRow row = new TableRow(this);
                    row.addView(makeCell(obj.optString("id")));
                    row.addView(makeCell(obj.optString("ticketName")));
                    row.addView(makeCell(obj.optString("ticketType")));
                    row.addView(makeCell(obj.optString("price")));
                    row.addView(makeCell(obj.optString("currentStock")));
                    row.addView(makeCell(obj.optString("totalStock")));
                    row.addView(makeCell(obj.optString("validDate")));
                    row.addView(makeCell(obj.optBoolean("isRefundable") ? "是" : "否"));
                    row.addView(makeCell(obj.optString("createTime").replace("T", " ")));
                    table.addView(row);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView makeCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextSize(15);
        return tv;
    }
}