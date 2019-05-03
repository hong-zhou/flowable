package com.hong.flowable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
public class FlowableApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowableApplication.class, args);
	}
}

@Service
@Log4j2
class EmailService {
	Map<String, AtomicInteger> sends = new ConcurrentHashMap<>();

	public void sendWelcomeEmail(String coustomerId, String email) {
		log.info("Sending welcome email for " + coustomerId + " to " + email);
		sends.computeIfAbsent(email, e -> new AtomicInteger());
		sends.get(email).incrementAndGet();
	}
}
