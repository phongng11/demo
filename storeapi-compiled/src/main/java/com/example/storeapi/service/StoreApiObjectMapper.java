package com.example.storeapi.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionException;

import com.fasterxml.jackson.core.type.TypeReference;

public class StoreApiObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** Parses the given JSON string into a Map. */
	    Map<String,Object> readValue(String content) {
	    try {
	        return this.readValue(content, new TypeReference<>(){});
	    } catch (IOException ioe) {
	        throw new CompletionException(ioe);
	    }
	}
}
