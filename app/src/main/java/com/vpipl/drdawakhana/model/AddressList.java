package com.vpipl.drdawakhana.model;

public class AddressList {
    String Name = "";
    String Address = "";
    String City = "0";
    String State = "";
    String PinCode = "0";
    String Mobilenumber = "";

    public AddressList(){}

    public String getName() {
        return this.Name;
    }

    public String getAddress() {
        return this.Address;
    }

    public String getCity() {
        return this.City;
    }

    public String getState() {
        return this.State;
    }

    public String getPinCode() {
        return this.PinCode;
    }

    public String getMobilenumber() {
        return this.Mobilenumber;
    }



    public void setName(String Name) {
        this.Name = Name;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public void setState(String State) {
        this.State = State;
    }

    public void setPinCode(String PinCode) {
        this.PinCode = PinCode;
    }

    public void setMobilenumber(String Mobilenumber) {
        this.Mobilenumber = Mobilenumber;
    }
}