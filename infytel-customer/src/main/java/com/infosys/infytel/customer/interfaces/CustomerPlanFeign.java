package com.infosys.infytel.customer.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.infosys.infytel.customer.dto.PlanDTO;

@FeignClient("PlanMS")
public interface CustomerPlanFeign {
	
	@RequestMapping(value = "/plans/{planId}")
	PlanDTO getPlan(@PathVariable("planId") Integer planId);

}
