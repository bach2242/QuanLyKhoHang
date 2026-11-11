package com.example.quanlykhohang;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvName, tvCategory, tvPrice, tvQuantity, tvLowStock, tvStockValue, tvDesc;
    private Button btnImportOne, btnSellOne, btnBack, btnDelete;
    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product currentProduct;
    private DecimalFormat priceFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        priceFormat = new DecimalFormat("#,###", symbols);

        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvQuantity = findViewById(R.id.tvDetailQuantity);
        tvLowStock = findViewById(R.id.tvDetailLowStock);
        tvStockValue = findViewById(R.id.tvDetailStockValue);
        tvDesc = findViewById(R.id.tvDetailDesc);

        btnImportOne = findViewById(R.id.btnImportOne);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDeleteProduct);

        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductDetails();

        // Nút Nhập thêm 1
        btnImportOne.setOnClickListener(v -> {
            if (currentProduct != null) {
                int newQty = currentProduct.getQuantity() + 1;
                dbHelper.updateQuantity(productId, newQty);
                loadProductDetails();
            }
        });

        // Nút Bán 1
        btnSellOne.setOnClickListener(v -> {
            if (currentProduct != null) {
                if (currentProduct.getQuantity() == 0) {
                    Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
                } else {
                    int newQty = currentProduct.getQuantity() - 1;
                    dbHelper.updateQuantity(productId, newQty);
                    loadProductDetails();
                }
            }
        });

        // Nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút Xóa sản phẩm
        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteProduct(productId);
            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadProductDetails() {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            finish();
            return;
        }

        tvName.setText(currentProduct.getName());
        tvCategory.setText(currentProduct.getCategory());
        tvPrice.setText(priceFormat.format(currentProduct.getPrice()) + " đ");
        tvQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        long stockValue = (long) currentProduct.getPrice() * currentProduct.getQuantity();
        tvStockValue.setText(priceFormat.format(stockValue) + " đ");

        if (currentProduct.getQuantity() < 5) {
            tvLowStock.setVisibility(View.VISIBLE);
        } else {
            tvLowStock.setVisibility(View.GONE);
        }

        String desc = currentProduct.getDescription();
        tvDesc.setText((desc == null || desc.isEmpty()) ? "Không có mô tả" : desc);
    }
}