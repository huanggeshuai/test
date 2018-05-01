package com.example.huang.test.entity;


import java.io.Serializable;
import java.math.BigDecimal;

public class Venues implements Serializable {
    private Integer venuesid;

    private Integer venuesUserCharge;

    private Integer venuesProvice;

    private Integer venuesCity;

    private Integer venuesAreas;

    private String venuesAddress;

    private BigDecimal venuesLongitude;

    private BigDecimal venuesLatitude;

    private String openTime;

    private String closeTime;

    private String venuesName;

    private Integer deletestate;

    private String cause;

    private String phone;

    private String brief;

    private String imageurl;

    private User userCharge;

    private Provinces provinces;

    private Cities cities;

    private Areas areas;

    public User getUserCharge() {
        return userCharge;
    }

    public void setUserCharge(User userCharge) {
        this.userCharge = userCharge;
    }

    public Provinces getProvinces() {
        return provinces;
    }

    public void setProvinces(Provinces provinces) {
        this.provinces = provinces;
    }

    public Cities getCities() {
        return cities;
    }

    public void setCities(Cities cities) {
        this.cities = cities;
    }

    public Areas getAreas() {
        return areas;
    }

    public void setAreas(Areas areas) {
        this.areas = areas;
    }

    public Integer getVenuesid() {
        return venuesid;
    }

    public void setVenuesid(Integer venuesid) {
        this.venuesid = venuesid;
    }

    public Integer getVenuesUserCharge() {
        return venuesUserCharge;
    }

    public void setVenuesUserCharge(Integer venuesUserCharge) {
        this.venuesUserCharge = venuesUserCharge;
    }

    public Integer getVenuesProvice() {
        return venuesProvice;
    }

    public void setVenuesProvice(Integer venuesProvice) {
        this.venuesProvice = venuesProvice;
    }

    public Integer getVenuesCity() {
        return venuesCity;
    }

    public void setVenuesCity(Integer venuesCity) {
        this.venuesCity = venuesCity;
    }

    public Integer getVenuesAreas() {
        return venuesAreas;
    }

    public void setVenuesAreas(Integer venuesAreas) {
        this.venuesAreas = venuesAreas;
    }

    public String getVenuesAddress() {
        return venuesAddress;
    }

    public void setVenuesAddress(String venuesAddress) {
        this.venuesAddress = venuesAddress == null ? null : venuesAddress.trim();
    }

    public BigDecimal getVenuesLongitude() {
        return venuesLongitude;
    }

    public void setVenuesLongitude(BigDecimal venuesLongitude) {
        this.venuesLongitude = venuesLongitude;
    }

    public BigDecimal getVenuesLatitude() {
        return venuesLatitude;
    }

    public void setVenuesLatitude(BigDecimal venuesLatitude) {
        this.venuesLatitude = venuesLatitude;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getVenuesName() {
        return venuesName;
    }

    public void setVenuesName(String venuesName) {
        this.venuesName = venuesName == null ? null : venuesName.trim();
    }

    public Integer getDeletestate() {
        return deletestate;
    }

    public void setDeletestate(Integer deletestate) {
        this.deletestate = deletestate;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause == null ? null : cause.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl == null ? null : imageurl.trim();
    }
}