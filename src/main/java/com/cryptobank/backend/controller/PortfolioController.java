package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.Portfolio;
import com.cryptobank.backend.model.ApiResponse;
import com.cryptobank.backend.repository.PortfolioDAO;
import com.cryptobank.backend.services.builders.ResponseBuilder;
import com.cryptobank.backend.services.generalServices.DepositService;
import com.cryptobank.backend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

	PortfolioDAO portfolioDAO;
	DepositService depositService;
	UserService userService;

	@GetMapping("/portfolioList")
	public ResponseEntity<ApiResponse<List<Portfolio>>> getPortfolioList(@RequestParam(defaultValue="") String sortOption, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="ACTIVE,WITHDRAWN,CLOSED") String portfolioStatus){
		//Check authentication and authorization
		if(!userService.checkUserAuthenticated(null)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		//Set HttpStatus
		HttpStatus responseStatus=HttpStatus.OK;
		//Calculate the offset attribute
		int limit=size;
		int offset=page*limit;
		//Get customer information
		String customer_id=String.valueOf(1);
		//Get customer's portfolio list  
		List<Portfolio> portfolioList=portfolioDAO.PaginationOrderByPortfolioId(customer_id,Arrays.asList(portfolioStatus.split(",")),sortOption,limit,offset);
		String message="Portfolio list found!";
		return ResponseBuilder.buildResposne(portfolioList, responseStatus, message);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Portfolio>> getPortfolioId(@PathVariable String id){
		//Check authentication and authorization
		if(!userService.checkUserAuthenticated(null)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		//Get customer
		Portfolio portfolio= portfolioDAO.findById(id).orElse(null);
		if(portfolio==null) {
			return ResponseBuilder.buildResposne(null, HttpStatus.NOT_FOUND,"Portfolio not found!");
		}
		return ResponseBuilder.buildResposne(portfolio, HttpStatus.OK, "Portfolio found!");
	}
	
	@PostMapping("/add")
//	public ResponseEntity<ApiResponse<String>> addPortfolio(@RequestBody Portfolio portfolio ){
//		String cryptoId=portfolio.getCryptoId();
//		Double cryptoAmount=portfolio.getCryptoAmount();
//		if(depositService.checkWallet(cryptoId,cryptoAmount)) {
//			//deposit and create portfolio
//			portfolioDAO.save(portfolio);
//			return ResponseBuilder.buildResposne("Success", HttpStatus.OK, "Portfolio Created");
//		}
//		return ResponseBuilder.buildResposne("Failed", HttpStatus.OK, "Insufficent amount or not created");
//	}
	
	@PutMapping("/")
	public ResponseEntity<ApiResponse<String>> updatePortfolio(){
		return null;
	}
	@DeleteMapping("/")
	public ResponseEntity<ApiResponse<String>> deletePortfolio(){
		return null;
	}
	
	
//	@PatchMapping("/")
//	public ResponseEntity<ApiResponse<String>>
	
	
	
	



	

}
