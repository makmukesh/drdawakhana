package com.vpipl.drdawakhana.model;

import android.graphics.Bitmap;

public class ProductsList {
    private String ID = "";
    private String code = "";
    private String UID = "0";
    private String Name = "";
    private String ImagePath = "";
    private String NewMRP = "0";
    private String NewDP = "0";

    private String Description = "";
    private String Detail = "";
    private String KeyFeature = "";
    private String Discount = "0";
    private String DiscountPer = "0";

    private String ShipCharge = "0";
    private String CatID = "";
    private String RandomNo = "";
    private String Qty = "1";
    private String BaseQty = "1";

    private String isProductNew = "";

    private String selectedSizeId = "0";
    private String selectedSizeName = "NA";
    private String selectedOptionId = "0";
    private String selectedOptionName = "NA";
    private String selectedColorId = "0";
    private String selectedColorName = "NA";
    private String selectedColorCode = "#ffffff";

    private String HeightIds = "0";
    private String WidthIds = "0";
    private String ColorIds = "0";
    private String MaterialIds = "0";

    private String DeliveryAddressID = "";
    private String DeliveryAddressFirstName = "";
    private String DeliveryAddressLastName = "";
    private String DeliveryAddress = "";
    private String DeliveryAddress1 = "";
    private String DeliveryAddress2 = "";
    private String DeliveryAddressCountryID = "";
    private String DeliveryAddressCountryName = "";
    private String DeliveryAddressStateCode = "";
    private String DeliveryAddressStateName = "";
    private String DeliveryAddressDistrict = "";
    private String DeliveryAddressCity = "";
    private String DeliveryAddressPinCode = "";
    private String DeliveryAddressEmail = "";
    private String DeliveryAddressMob = "";

    private Bitmap Imagebitmap = null;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getcode() {
        return code;
    }

    public void setcode(String code) {
        this.code = code;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
    }

    public Bitmap getImagebitmap() {
        return Imagebitmap;
    }

    public void setImagebitmap(Bitmap Imagebitmap) {
        this.Imagebitmap = Imagebitmap;
    }

    public String getNewMRP() {
        return NewMRP;
    }

    public void setNewMRP(String NewMRP) {
        this.NewMRP = NewMRP;
    }

    public String getNewDP() {
        return NewDP;
    }

    public void setNewDP(String NewDP) {
        this.NewDP = NewDP;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String Detail) {
        this.Detail = Detail;
    }

    public String getKeyFeature() {
        return KeyFeature;
    }

    public void setKeyFeature(String KeyFeature) {
        this.KeyFeature = KeyFeature;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String Discount) {
        this.Discount = Discount;
    }

    public String getDiscountPer() {
        return DiscountPer;
    }

    public void setDiscountPer(String DiscountPer) {
        this.DiscountPer = DiscountPer;
    }

    public String getShipCharge() {
        return ShipCharge;
    }

    public void setShipCharge(String ShipCharge) {
        this.ShipCharge = ShipCharge;
    }

    public String getCatID() {
        return CatID;
    }

    public void setCatID(String CatID) {
        this.CatID = CatID;
    }

    public String getRandomNo() {
        return RandomNo;
    }

    public void setRandomNo(String RandomNo) {
        this.RandomNo = RandomNo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }

    public String getBaseQty() {
        return BaseQty;
    }

    public void setBaseQty(String BaseQty) {
        this.BaseQty = BaseQty;
    }

    public String getIsProductNew() {
        return isProductNew;
    }

    public void setIsProductNew(String isProductNew) {
        this.isProductNew = isProductNew;
    }


    public String getselectedColorId() {
        return selectedColorId;
    }

    public void setselectedColorId(String selectedColorId) {
        this.selectedColorId = selectedColorId;
    }

    public String getselectedColorName() {
        return selectedColorName;
    }

    public void setselectedColorName(String selectedColorName) {
        this.selectedColorName = selectedColorName;
    }

    public String getDeliveryAddressID() {
        return DeliveryAddressID;
    }

    public void setDeliveryAddressID(String DeliveryAddressID) {
        this.DeliveryAddressID = DeliveryAddressID;
    }

    public String getDeliveryAddressFirstName() {
        return DeliveryAddressFirstName;
    }

    public void setDeliveryAddressFirstName(String DeliveryAddressFirstName) {
        this.DeliveryAddressFirstName = DeliveryAddressFirstName;
    }

    public String getDeliveryAddressLastName() {
        return DeliveryAddressLastName;
    }

    public void setDeliveryAddressLastName(String DeliveryAddressLastName) {
        this.DeliveryAddressLastName = DeliveryAddressLastName;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String DeliveryAddress) {
        this.DeliveryAddress = DeliveryAddress;
    }

    public String getDeliveryAddress1() {
        return DeliveryAddress1;
    }

    public void setDeliveryAddress1(String DeliveryAddress1) {
        this.DeliveryAddress1 = DeliveryAddress1;
    }

    public String getDeliveryAddress2() {
        return DeliveryAddress2;
    }

    public void setDeliveryAddress2(String DeliveryAddress2) {
        this.DeliveryAddress2 = DeliveryAddress2;
    }

    public String getDeliveryAddressCountryID() {
        return DeliveryAddressCountryID;
    }

    public void setDeliveryAddressCountryID(String DeliveryAddressCountryID) {
        this.DeliveryAddressCountryID = DeliveryAddressCountryID;
    }

    public String getDeliveryAddressCountryName() {
        return DeliveryAddressCountryName;
    }

    public void setDeliveryAddressCountryName(String DeliveryAddressCountryName) {
        this.DeliveryAddressCountryName = DeliveryAddressCountryName;
    }

    public String getDeliveryAddressStateCode() {
        return DeliveryAddressStateCode;
    }

    public void setDeliveryAddressStateCode(String DeliveryAddressStateCode) {
        this.DeliveryAddressStateCode = DeliveryAddressStateCode;
    }

    public String getDeliveryAddressStateName() {
        return DeliveryAddressStateName;
    }

    public void setDeliveryAddressStateName(String DeliveryAddressStateName) {
        this.DeliveryAddressStateName = DeliveryAddressStateName;
    }

    public String getDeliveryAddressDistrict() {
        return DeliveryAddressDistrict;
    }

    public void setDeliveryAddressDistrict(String DeliveryAddressDistrict) {
        this.DeliveryAddressDistrict = DeliveryAddressDistrict;
    }

    public String getDeliveryAddressCity() {
        return DeliveryAddressCity;
    }

    public void setDeliveryAddressCity(String DeliveryAddressCity) {
        this.DeliveryAddressCity = DeliveryAddressCity;
    }

    public String getDeliveryAddressPinCode() {
        return DeliveryAddressPinCode;
    }

    public void setDeliveryAddressPinCode(String DeliveryAddressPinCode) {
        this.DeliveryAddressPinCode = DeliveryAddressPinCode;
    }

    public String getDeliveryAddressEmail() {
        return DeliveryAddressEmail;
    }

    public void setDeliveryAddressEmail(String DeliveryAddressEmail) {
        this.DeliveryAddressEmail = DeliveryAddressEmail;
    }

    public String getDeliveryAddressMob() {
        return DeliveryAddressMob;
    }

    public void setDeliveryAddressMob(String DeliveryAddressMob) {
        this.DeliveryAddressMob = DeliveryAddressMob;
    }

    public String getSelectedSizeId() {
        return selectedSizeId;
    }

    public void setSelectedSizeId(String selectedSizeId) {
        this.selectedSizeId = selectedSizeId;
    }

    public String getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(String selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public String getSelectedColorId() {
        return selectedColorId;
    }

    public void setSelectedColorId(String selectedColorId) {
        this.selectedColorId = selectedColorId;
    }


    public String getSelectedSizeName() {
        return selectedSizeName;
    }

    public void setSelectedSizeName(String selectedSizeName) {
        this.selectedSizeName = selectedSizeName;
    }

    public String getSelectedOptionName() {
        return selectedOptionName;
    }

    public void setSelectedOptionName(String SelectedOptionName) {
        this.selectedOptionName = SelectedOptionName;
    }

    public String getSelectedColorName() {
        return selectedColorName;
    }

    public void setSelectedColorName(String selectedColorName) {
        this.selectedColorName = selectedColorName;
    }


    public String getSelectedColorCode() {
        return selectedColorCode;
    }

    public void setSelectedColorCode(String selectedColorCode) {
        this.selectedColorCode = selectedColorCode;
    }

 public void setHeightIds(String HeightIds) {
        this.HeightIds = HeightIds;
    }

 public void setWidthIds(String WidthIds) {
        this.WidthIds = WidthIds;
    }

 public void setColorIds(String ColorIds) {
        this.ColorIds = ColorIds;
    }

 public void setMaterialIds(String MaterialIds) {
        this.MaterialIds = MaterialIds;
    }

public String getHeightIds() {
        return HeightIds;
    }

public String getWidthIds() {
        return WidthIds;
    }

public String getColorIds() {
        return ColorIds;
    }

public String getMaterialIds() {
        return MaterialIds;
    }


}
