package io.globomart.prodcat.dto;

public class Product {
	private int productId;
	private String brandName;
	private String model;
	private String color;
	private String productType;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Product(int productId, String brandName, String model, String color,String productType) {
		this.productId = productId;
		this.brandName = brandName;
		this.model = model;
		this.color = color;
		this.productType = productType;
	}
	
	

	public Product(String brandName, String model, String color) {
		this.brandName = brandName;
		this.model = model;
		this.color = color;
	}

	public Product() {
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Product [productId=");
		builder.append(productId);
		builder.append(", brandName=");
		builder.append(brandName);
		builder.append(", model=");
		builder.append(model);
		builder.append(", color=");
		builder.append(color);
		builder.append("]");
		return builder.toString();
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
}
