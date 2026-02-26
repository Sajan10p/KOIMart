package com.koi.koimart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koi.koimart.R;
import com.koi.koimart.db.DbHelper;
import com.koi.koimart.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    private final Context context;
    private final List<CartItem> list;
    private final DbHelper db;
    private final OnCartChangedListener listener;

    public CartAdapter(Context context, List<CartItem> list, DbHelper db, OnCartChangedListener listener) {
        this.context = context;
        this.list = list;
        this.db = db;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartItem item = list.get(position);

        h.txtName.setText(item.name);
        h.txtPrice.setText("$" + String.format("%.2f", item.price));
        h.txtQty.setText(String.valueOf(item.qty));

        h.btnMinus.setOnClickListener(v -> {
            int q = item.qty - 1;
            if (q < 1) q = 1;
            item.qty = q;
            db.updateCartQty(item.cartId, q);
            notifyItemChanged(position);
            listener.onCartChanged();
        });

        h.btnPlus.setOnClickListener(v -> {
            int q = item.qty + 1;
            item.qty = q;
            db.updateCartQty(item.cartId, q);
            notifyItemChanged(position);
            listener.onCartChanged();
        });

        h.btnRemove.setOnClickListener(v -> {
            db.removeCartItem(item.cartId);
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQty;
        Button btnMinus, btnPlus, btnRemove;

        VH(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCartName);
            txtPrice = itemView.findViewById(R.id.txtCartPrice);
            txtQty = itemView.findViewById(R.id.txtCartQty);
            btnMinus = itemView.findViewById(R.id.btnCartMinus);
            btnPlus = itemView.findViewById(R.id.btnCartPlus);
            btnRemove = itemView.findViewById(R.id.btnCartRemove);
        }
    }
}
