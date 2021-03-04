package com.example.storeapi.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.storeapi.model.ProductInfo;
import com.example.storeapi.model.ProductInfo.Sku;

interface ProductInfoService {
	ProductInfo getProductInfo(String productId, String storeId) throws Exception;
	
	default boolean getAvailable(String sku, Map<String, Object> resultA) {
		HashMap<String, Object>[] avails = (HashMap<String, Object>[])resultA.get("availability");
		for(HashMap<String, Object> entry : avails) {
			if( ((String)sku).equalsIgnoreCase(entry.get("sku").toString()) && 
					Integer.valueOf((String)resultA.get("stock_level"))>0 ){
				return true;
			}
		}
		
		return false;
	}
}

public enum StoreService implements ProductInfoService{
	
	StoreA(new StoreAImpl()),
	StoreB(new StoreBImpl()), 
	StoreC(new StoreCImpl());
		
	
	private final ProductInfoService productInfoImpl;
	
	StoreService(ProductInfoService productInfoImpl){
		this.productInfoImpl = productInfoImpl;
	}

	
	public ProductInfo getProductInfo(String productId, String storeId) throws Exception {
		return this.productInfoImpl.getProductInfo(productId, storeId);
	}
}



class StoreAImpl implements ProductInfoService {
	
	@Override
	public ProductInfo getProductInfo(String productId, String storeId) throws URISyntaxException, IOException, InterruptedException {
		
		StoreApiObjectMapper objectMapper = new StoreApiObjectMapper();

		// need to create request header with accept type as json
		String baseUrl = "https://api.storeB.com/v12";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(baseUrl + "/product/get/" + productId))
				.header("Accept", "application/json")
				.GET().build();
				
		
		String body = HttpClient.newHttpClient()
		  .send(request, BodyHandlers.ofString())
		  .body();
		
		Map<String, Object> result = objectMapper.readValue(body);
	
		
		ProductInfo pi = new ProductInfo();
		pi.id = (String)result.get("product_id");
		pi.title = (String)result.get("title");
		pi.price = Double.valueOf((String)result.get("sale_price"));
		pi.imageUrl = (String)result.get("product_image");
		//pi.sku = new ProductInfo.Sku();
		List<Sku> skus = new ArrayList<>();
		// If needed we can use the objectMapper to map it to HashMap
		HashMap<String, Object>[] upcs = (HashMap<String, Object>[])result.get("upcs");
		for(HashMap<String, Object> entry : upcs) {
			Sku sku = new Sku();
			sku.upc = (String)entry.get("upc");
			sku.color = (String)entry.get("color");
			sku.size = (String)entry.get("size");
			sku.isAvailable = Boolean.valueOf((String)entry.get("is_available"));
			sku.productImage = (String)entry.get("product_image");
			skus.add(sku);
		}
		pi.sku = skus.toArray(new Sku[skus.size()]);
		
		return pi;
	}
}

class StoreBImpl implements ProductInfoService {
	@Override
	public ProductInfo getProductInfo(String productId, String storeId) throws URISyntaxException, IOException, InterruptedException {
		
		StoreApiObjectMapper objectMapper = new StoreApiObjectMapper();
		
		String baseUrl = "https://api.storeB.com/v1";
		
		HttpRequest requestD = HttpRequest.newBuilder()
				.uri(new URI(baseUrl + "/product/details/id/" + productId))
				.header("Accept", "application/json")
				.GET().build();
		
		HttpRequest requestA = HttpRequest.newBuilder()
				.uri(new URI(baseUrl + "/product/details/id/" + productId))
				.header("Accept", "application/json")
				.GET().build();
				
		
		String bodyD = HttpClient.newHttpClient()
		  .send(requestD, BodyHandlers.ofString())
		  .body();
		
		String bodyA = HttpClient.newHttpClient()
				  .send(requestD, BodyHandlers.ofString())
				  .body();
		
		Map<String, Object> resultD = objectMapper.readValue(bodyD);
		
		Map<String, Object> resultA = objectMapper.readValue(bodyA);
		
		
		ProductInfo pi = new ProductInfo();
		pi.id = (String)resultD.get("product_id");
		pi.title = (String)resultD.get("title");
		pi.price = Double.valueOf((String)resultD.get("price"));
		pi.imageUrl = (String)resultD.get("productImageUrl");
		//pi.sku = new ProductInfo.Sku();
		List<Sku> skus = new ArrayList<>();
		// If needed we can use the objectMapper to map it to HashMap
		HashMap<String, Object>[] upcs = (HashMap<String, Object>[])resultD.get("upcs");
		for(HashMap<String, Object> entry : upcs) {
			Sku sku = new Sku();
			sku.upc = (String)entry.get("sku");
			sku.color = (String)entry.get("color");
			sku.size = (String)entry.get("size");
			sku.productImage = (String)entry.get("productImageUrl");
			sku.isAvailable = getAvailable(sku.upc, resultA);
			skus.add(sku);
		}
		pi.sku = skus.toArray(new Sku[skus.size()]);
		
		return pi;
	}
}

class StoreCImpl implements ProductInfoService {
	@Override
	public ProductInfo getProductInfo(String productId, String storeId) throws URISyntaxException, IOException, InterruptedException {
		
		StoreApiObjectMapper objectMapper = new StoreApiObjectMapper();
		
		HttpClient httpClient = HttpClient.newBuilder().build();
		// need to create request header with accept type as json
		
		String baseUrl = "https://api.storeC.com/rest/latest";
		
		HttpRequest requestD = HttpRequest.newBuilder()
				.uri(new URI(baseUrl + "/product/id/" + productId + "/get"))
				.header("Accept", "application/json")
				.GET().build();
		
		String bodyD = HttpClient.newHttpClient()
				  .send(requestD, BodyHandlers.ofString())
				  .body();
		
		Map<String, Object> resultD = objectMapper.readValue(bodyD);
		
		
		ProductInfo pi = new ProductInfo();
		pi.id = (String)resultD.get("id");
		pi.title = (String)resultD.get("name");
		pi.price = Double.valueOf((String)resultD.get("finalPrice"));
		pi.imageUrl = (String)resultD.get("productImage");
		//pi.sku = new ProductInfo.Sku();
		List<Sku> skus = new ArrayList<>();
		// If needed we can use the objectMapper to map it to HashMap
		HashMap<String, Object>[] colorVariants = (HashMap<String, Object>[])resultD.get("colorVariants");
		for(HashMap<String, Object> colorVariant : colorVariants) {
			// If needed we can use the objectMapper to map it to HashMap
			HashMap<String, Object>[] variants = (HashMap<String, Object>[])colorVariant.get("variants");
			for(HashMap<String, Object> variant : variants) {
				Sku sku = new Sku();
				sku.color = (String)colorVariant.get("color");
				sku.upc = (String)variant.get("sku");
				
				sku.size = (String)variant.get("size");
				sku.productImage = (String)variant.get("productImage");
				sku.isAvailable = Boolean.valueOf((String)variant.get("isAvailable"));
				skus.add(sku);
			}
			
		}
		pi.sku = skus.toArray(new Sku[skus.size()]);
		
		return pi;
	}
}
