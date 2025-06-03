package com.thomas.page.traveler.ticket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thomas.page.R;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<TicketInventory> ticketList;
    private Context context;

    public TicketAdapter(Context context, List<TicketInventory> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView stockText;
        Button buyButton;

        public TicketViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            stockText = itemView.findViewById(R.id.tvStock);
            buyButton = itemView.findViewById(R.id.btnBuy);
        }
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.traveler_item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketInventory ticket = ticketList.get(position);
        holder.stockText.setText("票余量: " + ticket.getCurrentStock());

        // 设置图片（本地资源示意）
        switch (ticket.getTicketName()) {
            case "圆明园":
                holder.imageView.setImageResource(R.drawable.yuanmingyuan);
                break;
            case "长春园":
                holder.imageView.setImageResource(R.drawable.changchunyuan);
                break;
            case "绮春园":
                holder.imageView.setImageResource(R.drawable.qichunyuan);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.placeholder);
                break;
        }

        holder.buyButton.setOnClickListener(v -> {
            String ticketName = ticket.getTicketName();
            android.content.Intent intent = null;
            if ("圆明园".equals(ticketName)) {
                intent = new android.content.Intent(context, com.thomas.page.traveler.ticket.purchase.YmyPurchaseActivity.class);
            } else if ("长春园".equals(ticketName)) {
                intent = new android.content.Intent(context, com.thomas.page.traveler.ticket.purchase.CcyPurchaseActivity.class);
            } else if ("绮春园".equals(ticketName)) {
                intent = new android.content.Intent(context, com.thomas.page.traveler.ticket.purchase.QcyPurchaseActivity.class);
            }
            if (intent != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "暂不支持该景区购票", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }
}


