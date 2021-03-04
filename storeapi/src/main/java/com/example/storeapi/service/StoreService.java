package com.example.storeapi.service;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.storeapi.model.ProductInfo;
import com.example.storeapi.model.ProductInfo.Sku;

interface ProductInfo {
	ProductInfo getProductInfo(String productId, String storeId);
}

public enum StoreService implements ProductInfo {
	
	StoreA() { 
		new StoreA();
	}, 
	StoreB()
	{
		@Override
		public ProductInfo getProductInfo(String productId, String storeId) {
			
			HttpClient httpClient = HttpClient.newBuilder().build();
			// need to create request header with accept type as json
			
			String baseUrl = "https://api.storeB.com/v1";
			LinkedHashMap<String, Object> resultD = httpClient.get(baseUrl + "/product/details/id/" + productId);
			
			LinkedHashMap<String, Object> resultA = httpClient.get(baseUrl + "/product/availability/id/" + productId);
			
			ProductInfo pi = new ProductInfo();
			pi.id = resultD.get("product_id");
			pi.title = resultD.get("title");
			pi.price = Double.valueOf(resultD.get("price"));
			pi.imageUrl = resultD.get("productImageUrl");
			//pi.sku = new ProductInfo.Sku();
			List<Sku> skus = new ArrayList<>();
			HashMap<String, Object>[] upcs = resultD.get("upcs");
			for(HashMap<String, Object> entry : upcs) {
				Sku sku = new Sku();
				sku.upc = entry.get("sku");
				sku.color = entry.get("color");
				sku.size = entry.get("size");
				sku.productImage = entry.get("productImageUrl");
				sku.isAvailable = getAvailable(sku.upc, resultA);
				skus.add(sku);
			}
			pi.sku = skus.toArray(new Sku[skus.size()]);
			
			return pi;
		}
	}, 
	StoreC(){
		@Override
		public ProductInfo getProductInfo(String productId, String storeId) {
			HttpClient httpClient = HttpClient.newBuilder().build();
			// need to create request header with accept type as json
			
			String baseUrl = "https://api.storeC.com/rest/latest";
			LinkedHashMap<String, Object> resultD = httpClient.get(baseUrl + "/product/id/" + productId + "/get");
			
		
			ProductInfo pi = new ProductInfo();
			pi.id = resultD.get("id");
			pi.title = resultD.get("name");
			pi.price = Double.valueOf(resultD.get("finalPrice"));
			pi.imageUrl = resultD.get("productImage");
			//pi.sku = new ProductInfo.Sku();
			List<Sku> skus = new ArrayList<>();
			HashMap<String, Object>[] colorVariants = resultD.get("colorVariants");
			for(HashMap<String, Object> colorVariant : colorVariants) {
				
				
				
				
				HashMap<String, Object>[] variants = colorVariant.get("variants");
				for(HashMap<String, Object> variant : variants) {
					Sku sku = new Sku();
					sku.color = colorVariant.get("color");
					sku.upc = variant.get("sku");
					
					sku.size = variant.get("size");
					sku.productImage = variant.get("productImage");
					sku.isAvailable = variant.get("isAvailable");
					skus.add(sku);
				}
				
			}
			pi.sku = skus.toArray(new Sku[skus.size()]);
			
			return pi;
		}
	};
		
	
	
	private String productId;
	private String storeId;
	
	public boolean getAvailable(String sku, LinkedHashMap<String, Object> resultA) {
		HashMap<String, Object>[] avails = resultA.get("availability");
		for(HashMap<String, Object> entry : avails) {
			if(sku.equalsIgnoreCase(entry.get("sku") && entry.get("stock_level")>0){
				return true;
			}
		}
		
		return false;
	}
}

class StoreA implements ProductInfo {
	@Override
	public ProductInfo getProductInfo(String productId, String storeId) {
		HttpClient httpClient = HttpClient.newBuilder().build();
		// need to create request header with accept type as json
		String baseUrl = "https://api.storeB.com/v12";
		LinkedHashMap<String, Object> result = httpClient.get(baseUrl + "/product/get/" + productId);
		
		
		ProductInfo pi = new ProductInfo();
		pi.id = result.get("product_id");
		pi.title = result.get("title");
		pi.price = Double.valueOf(result.get("sale_price"));
		pi.imageUrl = result.get("product_image");
		//pi.sku = new ProductInfo.Sku();
		List<Sku> skus = new ArrayList<>();
		HashMap<String, Object>[] upcs = result.get("upcs");
		for(HashMap<String, Object> entry : upcs) {
			Sku sku = new Sku();
			sku.upc = entry.get("upc");
			sku.color = entry.get("color");
			sku.size = entry.get("size");
			sku.isAvailable = entry.get("is_available");
			sku.productImage = entry.get("product_image");
			skus.add(sku);
		}
		pi.sku = skus.toArray(new Sku[skus.size()]);
		
		return pi;
	}
}

class StoreB implements ProductInfo() {

}
