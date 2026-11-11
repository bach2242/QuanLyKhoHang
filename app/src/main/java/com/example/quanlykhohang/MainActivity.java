package com.example.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts, tvTotalValue, tvLowStockCount, tvEmptyList;
    private RecyclerView rvProducts;
    private Button btnAddProduct;
    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private DecimalFormat priceFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        priceFormat = new DecimalFormat("#,###", symbols);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        rvProducts = findViewById(R.id.rvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, new ArrayList<>());
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Product> products = dbHelper.getAllProducts();
        adapter.setProductList(products);

        // Xử lý khi danh sách rỗng
        if (products.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }

        // Cập nhật khối thống kê
        tvTotalProducts.setText(String.valueOf(products.size()));
        tvTotalValue.setText(priceFormat.format(dbHelper.getTotalValue()) + " đ");
        tvLowStockCount.setText(String.valueOf(dbHelper.countLowStock()));
    }
}