package com.example.quanlykhohang;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private final DecimalFormat priceFormat;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        priceFormat = new DecimalFormat("#,###", symbols);
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText("Giá bán: " + priceFormat.format(p.getPrice()) + " đ");
        holder.tvCategory.setText(p.getCategory());
        holder.tvQuantity.setText("SL: " + p.getQuantity());

        if (p.getQuantity() < 5) {
            holder.tvQuantity.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvLowStockBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvQuantity.setTextColor(Color.parseColor("#212529"));
            holder.tvLowStockBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", p.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory, tvQuantity, tvLowStockBadge;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvLowStockBadge = itemView.findViewById(R.id.tvLowStockBadge);
        }
    }
}