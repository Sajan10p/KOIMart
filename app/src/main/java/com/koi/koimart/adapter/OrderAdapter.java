package com.koi.koimart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koi.koimart.R;
import com.koi.koimart.model.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    private final Context context;
    private final List<OrderItem> list;

    public OrderAdapter(Context context, List<OrderItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        OrderItem o = list.get(position);
        h.txtOrderId.setText("Order #" + o.orderId);
        h.txtDate.setText("Date: " + o.orderDate);
        h.txtTotal.setText("Total: $" + String.format("%.2f", o.total));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtDate, txtTotal;

        VH(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtDate = itemView.findViewById(R.id.txtOrderDate);
            txtTotal = itemView.findViewById(R.id.txtOrderTotal);
        }
    }
}
