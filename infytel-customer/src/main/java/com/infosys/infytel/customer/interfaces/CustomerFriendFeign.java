package com.infosys.infytel.customer.interfaces;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("FriendMS")
public interface CustomerFriendFeign {
	
	@RequestMapping(value = "/customers/{phoneNo}")
	List<Long> getFamilyDetails(@PathVariable("phoneNo") Long phoneNo);

}
