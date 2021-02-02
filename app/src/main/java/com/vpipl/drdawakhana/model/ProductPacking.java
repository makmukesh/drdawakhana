package com.vpipl.drdawakhana.model;

/**
 * Created by Mukesh on 17-Nov-20.
 */

public class ProductPacking {
    private String ProductProdID = "";
    private String PackingText = "";
    private String PackingID = "";
    private String NewMRP = "";
    private String NewDP = "";

    public String getProductProdID() {
        return ProductProdID;
    }

    public void setProductProdID(String ProductProdID) {
        this.ProductProdID = ProductProdID;
    }

    public String getPackingText() {
        return PackingText;
    }

    public void setPackingText(String Productseqno) {
        this.PackingText = Productseqno;
    }

    public String getPackingID() {
        return PackingID;
    }

    public void setPackingID(String ProductSizeId) {
        this.PackingID = ProductSizeId;
    }

    public String getNewMRP() {
        return NewMRP;
    }

    public void setNewMRP(String ProductSizeName) {
        this.NewMRP = ProductSizeName;
    }

    public String getNewDP() {
        return NewDP;
    }

    public void setNewDP(String ProductSize) {
        this.NewDP = ProductSize;
    }
}
