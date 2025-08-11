package com.moyoy.api.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	@GetMapping({"/", "/health"})
	public String health() {
		return "Server Status : UP";
	}
}
