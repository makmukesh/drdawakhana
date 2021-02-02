package com.vpipl.drdawakhana.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vpipl.drdawakhana.model.ProductPacking;
import com.vpipl.drdawakhana.model.ProductsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mukesh on 17-Nov-20.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ProductsMaster";
    // Contacts table name
    private static final String TABLE_Products = "ProductsList";

    private static final String TABLE_Wishlist = "WishList";

    // Contacts Table Columns names
    private static final String KEY_ProdID = "id";
    private static final String KEY_ProductName = "ProductName";
    private static final String KEY_ProductCode = "ProductCode";
    private static final String KEY_Discount = "Discount";
    private static final String KEY_DiscountPer = "DiscountPer";
    private static final String KEY_NewDisp = "NewDisp";
    private static final String KEY_NewImgPath = "NewImgPath";
    private static final String KEY_NewMRP = "NewMRP";
    private static final String KEY_NewDP = "NewDP";

    private static final String KEY_HeightID = "HeightID";
    private static final String KEY_WidthID = "WidthID";
    private static final String KEY_ColorID = "ColorID";
    private static final String KEY_MaterialID = "MaterialID";


    private static final String KEY_RandomNo = "RandomNo";
    private static final String KEY_Uid = "Uid";
    private static final String KEY_Qty = "Qty";
    private static final String KEY_BaseQty = "BaseQty";
    private static final String KEY_CatId = "CatId";
    private static final String KEY_ShipCharge = "ShipCharge";
    private static final String KEY_SelectedSizeName = "SelectedSizeName";
    private static final String KEY_SelectedSizeId = "SelectedSizeId";
    private static final String KEY_selectedColorName = "selectedColorName";
    private static final String KEY_selectedColorId = "selectedColorId";
    private static final String KEY_SelectedOptionName = "SelectedOptionName";
    private static final String KEY_SelectedOptionId = "SelectedOptionId";
    private static final String KEY_ImagePath = "ImagePath";


    private static final String KEY_PackingID = "PackingID";
    private static final String KEY_PackingText = "PackingText";

    private static DatabaseHandler sqLiteHelper = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (sqLiteHelper == null)
            return new DatabaseHandler(context);
        else
            return sqLiteHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_Products +
                "(" + KEY_ProdID + " INTEGER PRIMARY KEY,"
                + KEY_ProductName + " TEXT,"
                + KEY_ProductCode + " TEXT,"
                + KEY_Discount + " TEXT,"
                + KEY_DiscountPer + " TEXT,"
                + KEY_NewDisp + " TEXT,"
                + KEY_NewImgPath + " TEXT,"
                + KEY_NewMRP + " TEXT,"
                + KEY_NewDP + " TEXT,"
                + KEY_HeightID + " TEXT,"
                + KEY_WidthID + " TEXT,"
                + KEY_ColorID + " TEXT,"
                + KEY_MaterialID + " TEXT"
                + ")";

        String CREATE_Wishlist_TABLE = "CREATE TABLE " + TABLE_Wishlist +
                "(" + KEY_ProdID + " INTEGER PRIMARY KEY,"
                + KEY_RandomNo + " TEXT,"
                + KEY_Uid + " TEXT,"
                + KEY_ProductName + " TEXT,"
                + KEY_ProductCode + " TEXT,"
                + KEY_NewMRP + " TEXT,"
                + KEY_NewDP + " TEXT,"
                + KEY_DiscountPer + " TEXT,"
                + KEY_Qty + " TEXT,"
                + KEY_BaseQty + " TEXT,"
                + KEY_CatId + " TEXT,"
                + KEY_ShipCharge + " TEXT,"
                + KEY_SelectedSizeName + " TEXT,"
                + KEY_SelectedSizeId + " TEXT,"
                + KEY_selectedColorName + " TEXT,"
                + KEY_selectedColorId + " TEXT,"
                + KEY_SelectedOptionName + " TEXT,"
                + KEY_SelectedOptionId + " TEXT,"
                + KEY_ImagePath + " TEXT"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_Wishlist_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Products);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Wishlist);
        onCreate(db);
    }

    // Adding new contact
    public void addProducts(ProductsList productsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ProdID, productsList.getID());
        values.put(KEY_ProductName, productsList.getName());
        values.put(KEY_ProductCode, productsList.getcode());
        values.put(KEY_Discount, productsList.getDiscount());
        values.put(KEY_DiscountPer, productsList.getDiscountPer());
        values.put(KEY_NewDisp, productsList.getIsProductNew());
        values.put(KEY_NewImgPath, productsList.getImagePath());
        values.put(KEY_NewMRP, productsList.getNewMRP());
        values.put(KEY_NewDP, productsList.getNewDP());
        values.put(KEY_HeightID, productsList.getHeightIds());
        values.put(KEY_WidthID, productsList.getWidthIds());
        values.put(KEY_ColorID, productsList.getColorIds());
        values.put(KEY_MaterialID, productsList.getMaterialIds());

        db.insert(TABLE_Products, null, values);
        db.close();
    }

    public void addProductsWishlist(ProductsList productsList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ProdID, productsList.getID());
        values.put(KEY_RandomNo, productsList.getRandomNo());
        values.put(KEY_Uid, productsList.getUID());
        values.put(KEY_ProductName, productsList.getName());
        values.put(KEY_ProductCode, productsList.getcode());
        values.put(KEY_NewMRP, productsList.getNewMRP());
        values.put(KEY_NewDP, productsList.getNewDP());
        values.put(KEY_DiscountPer, productsList.getDiscountPer());
        values.put(KEY_Qty, productsList.getQty());
        values.put(KEY_BaseQty, productsList.getBaseQty());
        values.put(KEY_CatId, productsList.getCatID());
        values.put(KEY_ShipCharge, productsList.getShipCharge());
        values.put(KEY_SelectedSizeName, productsList.getSelectedSizeName());
        values.put(KEY_SelectedSizeId, productsList.getSelectedSizeId());
        values.put(KEY_selectedColorName, productsList.getSelectedColorName());
        values.put(KEY_selectedColorId, productsList.getSelectedColorId());
        values.put(KEY_SelectedOptionName, productsList.getSelectedOptionName());
        values.put(KEY_SelectedOptionId, productsList.getSelectedOptionId());
        values.put(KEY_ImagePath, productsList.getImagePath());

        db.insert(TABLE_Wishlist, null, values);
        db.close();
    }

    // Getting All Contacts
    public List<ProductsList> getAllProducts(int sortcondition) {
        List<ProductsList> productsList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_ProdID + " DESC";

        if (sortcondition == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_ProdID + " DESC";
        } else if (sortcondition == 1) {
            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_ProductName + " ASC";
        }

//        if (sortcondition == 1) {
//            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_NewDP + " ASC";
//        } else if (sortcondition == 2) {
//            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_NewDP + " DESC";
//        } else if (sortcondition == 3) {
//            selectQuery = "SELECT  * FROM " + TABLE_Products + " ORDER BY " + KEY_DiscountPer + " DESC";
//        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProductsList products = new ProductsList();

                products.setID((cursor.getString(0)));
                products.setName(cursor.getString(1));
                products.setcode(cursor.getString(2));
                products.setDiscount(cursor.getString(3));
                products.setDiscountPer(cursor.getString(4));
                products.setIsProductNew(cursor.getString(5));
                products.setImagePath(cursor.getString(6));
                products.setNewMRP(cursor.getString(7));
                products.setNewDP(cursor.getString(8));

                products.setHeightIds(cursor.getString(9));
                products.setWidthIds(cursor.getString(10));
                products.setColorIds(cursor.getString(11));
                products.setMaterialIds(cursor.getString(12));

                productsList.add(products);

            } while (cursor.moveToNext());
        }
        return productsList;
    }

    public List<ProductsList> getAllProductsWishlist() {
        List<ProductsList> productsList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_Wishlist + "";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                ProductsList products = new ProductsList();

                products.setID((cursor.getString(0)));
                products.setRandomNo((cursor.getString(1)));
                products.setUID((cursor.getString(2)));
                products.setName(cursor.getString(3));
                products.setcode(cursor.getString(4));
                products.setNewMRP(cursor.getString(5));
                products.setNewDP(cursor.getString(6));
                products.setDiscountPer(cursor.getString(7));
                products.setQty(cursor.getString(8));
                products.setBaseQty(cursor.getString(9));
                products.setCatID(cursor.getString(10));
                products.setShipCharge(cursor.getString(11));
                products.setSelectedSizeName(cursor.getString(12));
                products.setSelectedSizeId(cursor.getString(13));
                products.setSelectedColorName(cursor.getString(14));
                products.setSelectedColorId(cursor.getString(15));
                products.setSelectedOptionName(cursor.getString(16));
                products.setSelectedOptionId(cursor.getString(17));
                products.setImagePath(cursor.getString(18));

                productsList.add(products);

            } while (cursor.moveToNext());
        }
        return productsList;
    }

    public void DeleteProductFromWishlist(int Prodid) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_Wishlist + " WHERE " + KEY_ProdID + " = " + Prodid); //delete all rows in a table
        db.close();
    }

    // Getting All Contacts
    public List<ProductsList> getFilteredProducts(List<String> selectedWidthIds, List<String> selectedHeightIds, List<String> selectedMaterialIds, List<String> selectedColorIds) {
        List<ProductsList> productsList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_Products;

//        if (selectedWidthIds.size()>0 ||selectedHeightIds.size()>0 ||selectedMaterialIds.size()>0 ||selectedColorIds.size()>0  )
//        {
//            selectQuery = "SELECT  * FROM " + TABLE_Products +"WHERE "+KEY_WidthID+" IN ("+selectQuery+") AND "+ KEY_HeightID+" IN ? AND "+KEY_ColorID+" IN ? AND "+KEY_MaterialID+" IN ? ";
//        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProductsList products = new ProductsList();

                products.setID((cursor.getString(0)));
                products.setName(cursor.getString(1));
                products.setcode(cursor.getString(2));
                products.setDiscount(cursor.getString(3));
                products.setDiscountPer(cursor.getString(4));
                products.setIsProductNew(cursor.getString(5));
                products.setImagePath(cursor.getString(6));
                products.setNewMRP(cursor.getString(7));
                products.setNewDP(cursor.getString(8));

                products.setHeightIds(cursor.getString(9));
                products.setWidthIds(cursor.getString(10));
                products.setColorIds(cursor.getString(11));
                products.setMaterialIds(cursor.getString(12));

                boolean add = false;
                List<String> HeightIds = Arrays.asList(cursor.getString(9).split("\\s*,\\s*"));
                List<String> WidthIds = Arrays.asList(cursor.getString(10).split("\\s*,\\s*"));
                List<String> ColorIds = Arrays.asList(cursor.getString(11).split("\\s*,\\s*"));
                List<String> MaterialIds = Arrays.asList(cursor.getString(12).split("\\s*,\\s*"));

                if (HeightIds.containsAll(selectedHeightIds))
                    add = true;

                if (WidthIds.containsAll(selectedWidthIds))
                    add = true;

                if (ColorIds.containsAll(selectedColorIds))
                    add = true;

                if (MaterialIds.containsAll(selectedMaterialIds))
                    add = true;

                if (add)
                    productsList.add(products);

            } while (cursor.moveToNext());
        }
        return productsList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_Products;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_Products); //delete all rows in a table
        db.close();
    }

    public void clearWishlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_Wishlist); //delete all rows in a table
        db.close();
    }
    // Getting All Contacts
    public List<ProductPacking> getProductPacking(int ProductId) {
        List<ProductPacking> productsList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_Products + " WHERE " + KEY_ProdID + " = '" + ProductId + "' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProductPacking products = new ProductPacking();

                products.setProductProdID((cursor.getString(0)));
                products.setNewMRP(cursor.getString(1));
                products.setNewDP(cursor.getString(2));
                products.setPackingID(cursor.getString(3));
                products.setPackingText(cursor.getString(4));

                productsList.add(products);

            } while (cursor.moveToNext());
        }
        return productsList;
    }
    // Adding new contact
    public void addProductsPackingNew(String Query) {

        SQLiteDatabase db = this.getWritableDatabase();

        String Que = "INSERT INTO " + TABLE_Products + " (" + KEY_ProdID + ", " + KEY_NewMRP + ", " + KEY_NewDP + ", " + KEY_PackingID + ", " + KEY_PackingText + ") VALUES ";
        String Q = Que + Query;

        StringBuilder b = new StringBuilder(Q);
        b.replace(Q.lastIndexOf(","), Q.lastIndexOf(",") + 1, ";" );
        Q = b.toString();
        Q = Q.replaceAll("\"\"", "\"");
        db.execSQL(Q);
        db.close();

    }
}