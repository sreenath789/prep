package com.ecom.beans;

import java.beans.Transient;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "DigitalProducts")
public class DigitalProducts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private int productId;

	@Column(unique = true)
	String productCode;
	@Lob
	private String images;
	@Lob
	private String photos;

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	private String productName;
	private String vendor;
	private String productCategory;
	private String productSubcategory;
	private long size;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	private int rating;
	private double MRPPrice;
	private double discountPrice;
	private String description;
	private String productDetails;
	private String setHistoryPath;

	public String getSetHistoryPath() {
		return setHistoryPath;
	}

	public void setSetHistoryPath(String setHistoryPath) {
		this.setHistoryPath = setHistoryPath;
	}

	@Column(columnDefinition = "character(1) DEFAULT 'Y'::bpchar")
	private char isActive;
	private int createdBy;
	private LocalDate created;
	private int updatedBy;
	private LocalDate updated;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public double getMRPPrice() {
		return MRPPrice;
	}

	public void setMRPPrice(double mRPPrice) {
		MRPPrice = mRPPrice;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(String productDetails) {
		this.productDetails = productDetails;
	}

	public char getIsActive() {
		return isActive;
	}

	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}

	public LocalDate getCreated() {
		return created;
	}

	public void setCreated(LocalDate created) {
		this.created = created;
	}

	public LocalDate getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDate updated) {
		this.updated = updated;
	}

	@Transient
	public String getImagePath() {
		if (images == null || productId == 0)
			return null;
		return "/Digitaluploads/" + productId + "/" + images;

	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductSubcategory() {
		return productSubcategory;
	}

	public void setProductSubcategory(String productSubcategory) {
		this.productSubcategory = productSubcategory;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

}
