package com.thomas.page.admin.ticket;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.stream.IntStream;

public class TicketAddActivity extends AppCompatActivity {
    Spinner ticketNameSpinner, ticketTypeSpinner, operationTypeSpinner, ticketQuantitySpinner;
    Button submitBtn;

    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = "http://192.168.166.112:8080/api/ticket/add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ticket_add);

        ticketNameSpinner = findViewById(R.id.ticketNameSpinner);
        ticketTypeSpinner = findViewById(R.id.ticketTypeSpinner);
        operationTypeSpinner = findViewById(R.id.operationTypeSpinner);
        ticketQuantitySpinner = findViewById(R.id.ticketQuantitySpinner);
        submitBtn = findViewById(R.id.submitTicketBtn);

        // 设置数据源
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"圆明园", "长春园", "绮春园"});
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketNameSpinner.setAdapter(nameAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"成人票", "优惠票"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketTypeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<String> opAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"增加", "删除"});
        opAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operationTypeSpinner.setAdapter(opAdapter);

        String[] quantityOptions = IntStream.rangeClosed(1, 100)
                .mapToObj(String::valueOf).toArray(String[]::new);
        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantityOptions);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketQuantitySpinner.setAdapter(quantityAdapter);

        submitBtn.setOnClickListener(v -> sendTicketData());
    }

    private void sendTicketData() {
        try {
            JSONObject json = new JSONObject();
            json.put("ticketName", ticketNameSpinner.getSelectedItem().toString());
            json.put("ticketType", ticketTypeSpinner.getSelectedItem().toString());
            json.put("operationType", operationTypeSpinner.getSelectedItem().toString());
            json.put("addStock", Integer.parseInt(ticketQuantitySpinner.getSelectedItem().toString()));
            json.put("validDate", 30);
            json.put("isRefundable", true);

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder().url(BASE_URL).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(TicketAddActivity.this, "门票操作成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TicketAddActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
