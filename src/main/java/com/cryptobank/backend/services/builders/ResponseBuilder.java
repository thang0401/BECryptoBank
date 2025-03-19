package com.cryptobank.backend.services.builders;

import com.cryptobank.backend.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//@Service
public class ResponseBuilder {

	public static <T> ResponseEntity<ApiResponse<T>> buildResposne(T data,HttpStatus status,String message){
		ApiResponse<T> apiResponse=new ApiResponse<>();
		apiResponse.setMessage(message);
		apiResponse.setObject(data);
		return ResponseEntity.status(status).body(apiResponse);
	}

}
