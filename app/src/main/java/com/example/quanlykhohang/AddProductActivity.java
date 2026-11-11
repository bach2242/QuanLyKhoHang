package com.example.quanlykhohang;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtName, edtPrice, edtQuantity, edtDesc;
    private Spinner spnCategory;
    private Switch swActive;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtProductName);
        edtPrice = findViewById(R.id.edtProductPrice);
        edtQuantity = findViewById(R.id.edtProductQuantity);
        edtDesc = findViewById(R.id.edtProductDesc);
        spnCategory = findViewById(R.id.spnCategory);
        swActive = findViewById(R.id.swActive);
        btnSave = findViewById(R.id.btnSaveProduct);

        String[] categories = {"Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spnCategory.setAdapter(spinnerAdapter);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();
        int active = swActive.isChecked() ? 1 : 0;
        String desc = edtDesc.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Tên sản phẩm không được để trống");
            edtName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá bán");
            edtPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            return;
        }

        if (quantityStr.isEmpty()) {
            edtQuantity.setError("Vui lòng nhập số lượng");
            edtQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            return;
        }

        Product newProduct = new Product(name, price, quantity, category, active, desc);
        long id = dbHelper.insertProduct(newProduct);
        if (id != -1) {
            Toast.makeText(this, "Đã thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại MainActivity
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}