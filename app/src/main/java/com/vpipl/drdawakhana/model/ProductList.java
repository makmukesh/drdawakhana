package com.vpipl.drdawakhana.model;

public class ProductList {

    String productId = "";
    String productName = "";
    String productRate = "0";
    String productRateTitle = "";
    String productQty = "0";
    String productQtyTitle = "";
    String productQtyValue = "";
    String productImage = "";
    String productQtyType = "";
    String selectedPackingId = "0";
    String selectedPackingName = "NA";

    String NewMRP = "0";

    public ProductList(String productId, String productName, String productRate, String productRateTitle, String productQty, String productQtyTitle, String productQtyValue, String productImage, String selectedPackingId, String selectedPackingName, String NewMRP) {
        this.productId = productId;
        this.productName = productName;
        this.productRate = productRate;
        this.productRateTitle = productRateTitle;
        this.productQty = productQty;
        this.productQtyTitle = productQtyTitle;
        this.productQtyValue = productQtyValue;
        this.productImage = productImage;
        this.selectedPackingId = selectedPackingId;
        this.selectedPackingName = selectedPackingName;
        this.NewMRP = NewMRP;
    }

    public String getProductId() {
        return this.productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public String getProductRate() {
        return this.productRate;
    }

    public String getProductRateTitle() {
        return this.productRateTitle;
    }

    public String getProductQty() {
        return this.productQty;
    }

    public String getProductQtyTitle() {
        return this.productQtyTitle;
    }

    public String getProductQtyValue() {
        return this.productQtyValue;
    }

    public String getProductImage() {
        return this.productImage;
    }

    public String getProductQtyType() {
        return this.productQtyType;
    }

    public String getselectedPackingId() {
        return selectedPackingId;
    }

    public String getselectedPackingName() {
        return selectedPackingName;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public void setselectedPackingId(String selectedPackingId) {
        this.selectedPackingId = selectedPackingId;
    }

    public void setselectedPackingName(String selectedPackingName) {
        this.selectedPackingName = selectedPackingName;
    }
    public void setNewMRP(String NewMRP) {
        this.NewMRP = NewMRP;
    }

    public String getNewMRP() {
        return this.NewMRP;
    }

}