package com.thomas.page.traveler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import com.thomas.page.R;

public class ProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traveler_product_query);

        RecyclerView recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Product> productList = new ArrayList<>();
        TravelerProductAdapter adapter = new TravelerProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        // 拉取商品列表
        new Thread(() -> {
            try {
                String url = "http://192.168.166.112:8080/api/product/list";
                String response = com.thomas.page.util.HttpRequestUtil.getRequest(url);
                if (response != null) {
                    JSONArray arr = new JSONArray(response);
                    productList.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Product p = new Product();
                        p.id = obj.optInt("id");
                        p.name = obj.optString("name");
                        p.category = obj.optString("category");
                        p.price = obj.optDouble("price");
                        p.description = obj.optString("description");
                        p.stock = obj.optInt("stock");
                        p.imageUrl = obj.optString("imageUrl");
                        p.scenicSpotId = obj.optInt("scenicSpotId");
                        p.isOnSale = obj.optInt("isOnSale");
                        p.createTime = obj.optString("createTime");
                        productList.add(p);
                    }
                    runOnUiThread(adapter::notifyDataSetChanged);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

// Traveler端商品实体
class Product {
    public int id;
    public String name;
    public String category;
    public double price;
    public String description;
    public int stock;
    public String imageUrl;
    public int scenicSpotId;
    public int isOnSale;
    public String createTime;
}

// Traveler端商品Adapter
class TravelerProductAdapter extends RecyclerView.Adapter<TravelerProductAdapter.ViewHolder> {
    private List<Product> productList;
    private android.content.Context context;
    public TravelerProductAdapter(android.content.Context context, List<Product> list) {
        this.context = context;
        this.productList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.name.setText(p.name);
        holder.price.setText(String.format("%.2f元", p.price));
        holder.stock.setText("库存:" + p.stock);
        String imgUrl = p.imageUrl;
        if (imgUrl != null && !imgUrl.startsWith("http")) {
            imgUrl = "http://192.168.166.112:8080/products/" + imgUrl;
        }
        Glide.with(context).load(imgUrl).placeholder(R.drawable.ic_launcher_background).into(holder.image);
        holder.btnBuy.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.thomas.page.traveler.product.purchase.ProductPurchaseActivity.class);
            intent.putExtra("product_id", p.id);
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() { return productList.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView image;
        android.widget.TextView name, price, stock;
        android.widget.Button btnBuy;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            stock = itemView.findViewById(R.id.product_stock);
            btnBuy = itemView.findViewById(R.id.btn_buy_product);
        }
    }
}
