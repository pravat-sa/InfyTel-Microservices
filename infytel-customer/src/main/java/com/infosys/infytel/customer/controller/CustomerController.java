package com.infosys.infytel.customer.controller;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.dto.CustomerDTO;
import com.infosys.infytel.customer.dto.LoginDTO;
import com.infosys.infytel.customer.dto.PlanDTO;
import com.infosys.infytel.customer.service.CustomerHystixService;
import com.infosys.infytel.customer.service.CustomerService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@RestController
@CrossOrigin
@RefreshScope
@RibbonClient(name = "custribbon")
public class CustomerController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CustomerService custService;
	
	@Autowired
	CustomerHystixService hystrixService;

	// Create a new customer
	@PostMapping(value = "/customers",  consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer {}", custDTO);
		custService.createCustomer(custDTO);
	}

	// Login
	@PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for customer {} with password {}", loginDTO.getPhoneNo(),loginDTO.getPassword());
		return custService.login(loginDTO);
	}

	// Fetches full profile of a specific customer
	//@HystrixCommand(fallbackMethod = "getCustomerProfileFallback")
	@GetMapping(value = "/customers/{phoneNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) throws InterruptedException, ExecutionException {

		logger.info("Profile request for customer {}", phoneNo);
		System.out.println("=====In Profile ====="+ phoneNo);
		
		
		CustomerDTO custDTO = custService.getCustomerProfile(phoneNo);
		
		Future<List<Long>> family = hystrixService.getFamilyDetails(phoneNo);
		Future<PlanDTO> planDTO = hystrixService.getPlans(custDTO.getCurrentPlan().getPlanId());
		
		custDTO.setFriendAndFamily(family.get());
		custDTO.setCurrentPlan(planDTO.get());		
		
		return custDTO;
	}
	
	public CustomerDTO getCustomerProfileFallback(Long phoneNo) {
		System.out.println("======In fallback====="+ phoneNo);
		return new CustomerDTO();
	}



}
