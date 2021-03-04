package com.example.storeapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.storeapi.model.ProductInfo;
import com.example.storeapi.service.StoreService;

@RestController
@RequestMapping("/")
public class RESTController {
	

	@GetMapping("/storeApi/${storeId}/${productId}")
	public ProductInfo getProductInfo(@PathVariable String storeId, @PathVariable String productId) throws Exception {
		
		ProductInfo productInfo = null;
		
		// use Enum abstraction if PathVariable is of enumtype
		//productInfo = StoreService.StoreA.getProductInfo(productId, storeId);
		
		// Another way to iterate throug to find the match Impl class...
		//StoreService.values();
		
		switch(storeId) {
			case "storeA":
				productInfo = StoreService.StoreA.getProductInfo(productId, storeId);
				break;
			case "storeB":
				productInfo = StoreService.StoreB.getProductInfo(productId, storeId);
				break;
			case "storeC":
				productInfo = StoreService.StoreC.getProductInfo(productId, storeId);
				break;
			default:
				throw new Exception("unrecognized storeId");
		
		}
		
		return productInfo;
	}
}
