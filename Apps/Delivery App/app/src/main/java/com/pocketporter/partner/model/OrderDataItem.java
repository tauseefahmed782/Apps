package com.pocketporter.partner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDataItem implements Parcelable {

	@SerializedName("transaction_id")
	private String transactionId;

	@SerializedName("wall_amt")
	private String wallAmt;

	@SerializedName("p_method_name")
	private String pMethodName;

	@SerializedName("cou_amt")
	private String couAmt;

	@SerializedName("cat_img")
	private String catImg;

	@SerializedName("orderid")
	private String orderid;

	@SerializedName("cat_name")
	private String catName;



	@SerializedName("v_type")
	private String vType;

	@SerializedName("package_number")
	private String packageNumber;

	@SerializedName("order_status")
	private String orderStatus;

	@SerializedName("v_img")
	private String vImg;

	@SerializedName("pick_lat")
	private double pickLat;



	@SerializedName("date_time")
	private String dateTime;

	@SerializedName("pick_lng")
	private double pickLng;

	@SerializedName("subtotal")
	private String subtotal;

	@SerializedName("p_method_img")
	private String pMethodImg;

	@SerializedName("rider_name")
	private String riderName;

	@SerializedName("pick_address")
	private String pickAddress;

	@SerializedName("p_total")
	private String pTotal;

	@SerializedName("pick_name")
	private String pickName;

	@SerializedName("pick_mobile")
	private String pickMobile;



	@SerializedName("rider_lats")
	private double riderLat;

	@SerializedName("rider_longs")
	private double riderLongs;


	@SerializedName("parceldata")
	@Expose
	private List<Dropitem> parceldata = null;

	protected OrderDataItem(Parcel in) {
		transactionId = in.readString();
		wallAmt = in.readString();
		pMethodName = in.readString();
		couAmt = in.readString();
		catImg = in.readString();
		orderid = in.readString();
		catName = in.readString();

		vType = in.readString();
		packageNumber = in.readString();
		orderStatus = in.readString();
		vImg = in.readString();
		pickLat = in.readDouble();

		dateTime = in.readString();
		pickLng = in.readDouble();
		subtotal = in.readString();
		pMethodImg = in.readString();
		riderName = in.readString();
		pickAddress = in.readString();
		pTotal = in.readString();
		pickName = in.readString();
		pickMobile = in.readString();

		riderLat = in.readDouble();
		riderLongs = in.readDouble();

	}

	public static final Creator<OrderDataItem> CREATOR = new Creator<OrderDataItem>() {
		@Override
		public OrderDataItem createFromParcel(Parcel in) {
			return new OrderDataItem(in);
		}

		@Override
		public OrderDataItem[] newArray(int size) {
			return new OrderDataItem[size];
		}
	};

	public String getTransactionId(){
		return transactionId;
	}

	public String getWallAmt(){
		return wallAmt;
	}

	public String getPMethodName(){
		return pMethodName;
	}

	public String getCouAmt(){
		return couAmt;
	}

	public String getCatImg(){
		return catImg;
	}

	public String getOrderid(){
		return orderid;
	}

	public String getCatName(){
		return catName;
	}



	public String getVType(){
		return vType;
	}

	public String getPackageNumber(){
		return packageNumber;
	}

	public String getOrderStatus(){
		return orderStatus;
	}

	public String getVImg(){
		return vImg;
	}

	public double getPickLat(){
		return pickLat;
	}



	public String getDateTime(){
		return dateTime;
	}

	public double getPickLng(){
		return pickLng;
	}

	public String getSubtotal(){
		return subtotal;
	}

	public String getPMethodImg(){
		return pMethodImg;
	}

	public String getRiderName(){
		return riderName;
	}

	public String getPickAddress(){
		return pickAddress;
	}

	public String getPTotal(){
		return pTotal;
	}

	public String getPickName() {
		return pickName;
	}

	public void setPickName(String pickName) {
		this.pickName = pickName;
	}

	public String getPickMobile() {
		return pickMobile;
	}

	public void setPickMobile(String pickMobile) {
		this.pickMobile = pickMobile;
	}

	public List<Dropitem> getParceldata() {
		return parceldata;
	}

	public void setParceldata(List<Dropitem> parceldata) {
		this.parceldata = parceldata;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(transactionId);
		parcel.writeString(wallAmt);
		parcel.writeString(pMethodName);
		parcel.writeString(couAmt);
		parcel.writeString(catImg);
		parcel.writeString(orderid);
		parcel.writeString(catName);
		parcel.writeString(vType);
		parcel.writeString(packageNumber);
		parcel.writeString(orderStatus);
		parcel.writeString(vImg);
		parcel.writeDouble(pickLat);
		parcel.writeString(dateTime);
		parcel.writeDouble(pickLng);
		parcel.writeString(subtotal);
		parcel.writeString(pMethodImg);
		parcel.writeString(riderName);
		parcel.writeString(pickAddress);
		parcel.writeString(pTotal);
		parcel.writeString(pickName);
		parcel.writeString(pickMobile);
		parcel.writeDouble(riderLat);
		parcel.writeDouble(riderLongs);
	}
}