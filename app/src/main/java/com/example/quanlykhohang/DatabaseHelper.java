package com.example.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "warehouse.db";
    public static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // Bảng products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PROD_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_CATEGORY = "category";
    public static final String COL_ACTIVE = "active";
    public static final String COL_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL)";

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PRICE + " INTEGER NOT NULL, " +
                COL_QUANTITY + " INTEGER NOT NULL, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_ACTIVE + " INTEGER NOT NULL, " +
                COL_DESCRIPTION + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createProductsTable);

        // Đổi tài khoản đăng nhập thành bachlx (Tên bạn) / mật khẩu 123456
        insertUserInternal(db, "bachlx", "123456");

        // Chèn bộ sản phẩm mẫu hoàn toàn mới
        insertProductInternal(db, new Product("Màn hình Dell 24 inch", 2550000, 12, "Điện tử", 1, "Màn hình IPS viền mỏng, tần số quét 75Hz"));

        // Cố tình để số lượng = 2 để test giao diện nhãn đỏ "Sắp hết hàng"
        insertProductInternal(db, new Product("Cáp sạc Type-C", 85000, 2, "Điện tử", 1, "Cáp sạc bọc dù chống đứt 2 mét"));

        insertProductInternal(db, new Product("Thùng giấy in A4", 350000, 25, "Văn phòng phẩm", 1, "Giấy in Double A định lượng 70gsm"));

        insertProductInternal(db, new Product("Balo Laptop Xiaomi", 420000, 10, "Khác", 1, "Balo chống nước, vừa laptop 15.6 inch"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    private long insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    private long insertProductInternal(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, p.getName());
        values.put(COL_PRICE, p.getPrice());
        values.put(COL_QUANTITY, p.getQuantity());
        values.put(COL_CATEGORY, p.getCategory());
        values.put(COL_ACTIVE, p.getActive());
        values.put(COL_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // 1. Thêm tài khoản mới vào bảng users
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertUserInternal(db, username, password);
    }

    // 2. Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 3. Thêm sản phẩm, trả về id vừa thêm
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertProductInternal(db, p);
    }

    // 4. Lấy toàn bộ sản phẩm để hiển thị danh sách
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                Product p = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
                );
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 5. Lấy một sản phẩm theo id
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PROD_ID + " = ?",
                new String[]{String.valueOf(id)});

        Product p = null;
        if (cursor.moveToFirst()) {
            p = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
            );
        }
        cursor.close();
        return p;
    }

    // 6. Cập nhật số lượng (Nhập thêm / Bán)
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COL_PROD_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 7. Xóa sản phẩm theo id
    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COL_PROD_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 8. Tổng giá trị kho: SUM(price * quantity)
    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_PRODUCTS, null);
        long total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    // 9. Số sản phẩm sắp hết hàng: COUNT với quantity < 5
    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_QUANTITY + " < ?",
                new String[]{"5"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}