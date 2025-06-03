package com.thomas.page.admin.product;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.thomas.page.R;
import com.thomas.page.util.HttpRequestUtil;
import com.thomas.page.admin.product.model.Product;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductQueryActivity extends AppCompatActivity {
    private LinearLayout cardContainer;
    private TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_query);

        // 初始化视图
        cardContainer = findViewById(R.id.card_container);
        textTitle = findViewById(R.id.text_title);

        // 从后端获取商品数据
        fetchProducts();
    }

    private void fetchProducts() {
        new Thread(() -> {
            try {
                String url = "http://192.168.166.112:8080/api/product/list";
                String response = HttpRequestUtil.getRequest(url);
                JSONArray jsonArray = new JSONArray(response);
                List<Product> products = new ArrayList<>();

                // 解析JSON数据
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    Product product = new Product(
                        json.getString("name"),
                        json.getDouble("price"),
                        json.getInt("stock"),
                        json.getString("description"),
                        json.getString("imageUrl")
                    );
                    products.add(product);
                }

                // 在主线程中更新UI
                runOnUiThread(() -> {
                    updateUI(products);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "获取商品信息失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void updateUI(List<Product> products) {
        // 清空现有卡片
        cardContainer.removeAllViews();

        // 创建新的卡片布局
        LinearLayout rowLayout = null;
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            // 每两个商品创建一行
            if (i % 2 == 0) {
                rowLayout = new LinearLayout(this);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                cardContainer.addView(rowLayout);
            }

            // 创建商品卡片
            CardView cardView = createProductCard(product);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            );
            params.setMargins(8, 8, 8, 8);
            cardView.setLayoutParams(params);
            rowLayout.addView(cardView);
        }
    }

    private CardView createProductCard(Product product) {
        // 创建卡片视图
        CardView cardView = new CardView(this);
        cardView.setRadius(16);
        cardView.setElevation(6);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        // 创建卡片内容布局
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 添加商品图片
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            200
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this)
            .load("http://192.168.166.112:8080/products/" + product.getImageUrl())
            .into(imageView);
        contentLayout.addView(imageView);

        // 添加商品名称
        TextView nameText = new TextView(this);
        nameText.setText(product.getName());
        nameText.setTextSize(18);
        nameText.setTextColor(getResources().getColor(android.R.color.black));
        nameText.setPadding(16, 16, 16, 8);
        contentLayout.addView(nameText);

        // 添加商品价格
        TextView priceText = new TextView(this);
        priceText.setText(String.format("价格: ¥%.2f", product.getPrice()));
        priceText.setTextSize(16);
        priceText.setPadding(16, 0, 16, 8);
        contentLayout.addView(priceText);

        // 添加库存信息
        TextView stockText = new TextView(this);
        stockText.setText(String.format("库存: %d", product.getStock()));
        stockText.setTextSize(16);
        stockText.setPadding(16, 0, 16, 16);
        contentLayout.addView(stockText);

        cardView.addView(contentLayout);
        return cardView;
    }
}