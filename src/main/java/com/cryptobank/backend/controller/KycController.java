package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.KycRequest;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Kyc")
public class KycController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDAO userDao;
	
	@PutMapping("/ventify/{nation}/{userId}")
	public ResponseEntity<String> ventifyKYC(@RequestBody KycRequest request,@PathVariable String id,HttpSession session,@PathVariable String nation)
	{
		try {
			User userKYC = userService.getUserKYC(id);
			userKYC.setFirstName(request.getFirstName());
			userKYC.setLastName(request.getLastName());
			userKYC.setGender(request.getGender());
			userKYC.setPhoneNumber(request.getPhone());
			userKYC.setDateOfBirth(request.getDateOfBirth());
			userKYC.setHomeAddress(request.getAddress());
			userKYC.setWard(request.getWard());
			userKYC.setDistrict(request.getDistrict());
			userKYC.setProvince(request.getProvince());
			userKYC.setIdCardFrontImgUrl(request.getIdCardFrontImgUrl());
			userKYC.setIdCardBackImgUrl(request.getIdCardBackImgUrl());
			userKYC.setEmail(request.getEmail());
			userKYC.setNation(request.getNation());
			userKYC.setIdCardNumber(request.getIdNumber());
			userDao.save(userKYC);
			return ResponseEntity.ok("Xác thực người dùng thành công");
		} catch (Exception e) {
			return ResponseEntity.status(401).body("Xác thực người dùng (KYC) thất bại");
		}
		
	}
	
}
