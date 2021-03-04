package com.example.storeapi.model;

/**
 * Id, 
 * title, 
 * imageUrl, 
 * price, 
 * availability and 
 * all sku combinations (medium-black, medium-red, small-black, small-red and so on).
 * @author pnguyen
 *
 */
public class ProductInfo {
	public String id;
	public String title;
	public String imageUrl;
	public double price;
	public Sku[] sku;
	
	
	public static class Sku {
		public String upc;
		public String color;
		public String size;
		public boolean isAvailable;
		public String productImage;
	}
}

