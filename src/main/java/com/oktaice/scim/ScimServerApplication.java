package com.oktaice.scim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScimServerApplication {

	/**
     * TODO: (C) Implement Rate Limiting (https://developer.okta.com/standards/SCIM/#rate-limiting)
	 * @param args
	 */

	public static void main(String[] args) {
		SpringApplication.run(ScimServerApplication.class, args);
	}

}
