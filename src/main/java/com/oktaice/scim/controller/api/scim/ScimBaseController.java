package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.scim.ScimExceptionResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles errors and the SCIM configuration endpoints.
 * The SCIM Configuration endpoints presents static JSON configuration files (under resources/scim-json) as output.
 */
@RestController
@RequestMapping("/scim/v2")
public class ScimBaseController {

    /**
     * Return the features supported by the ICE Research SCIM API.
     * i.e.: The ICE Research API supports patchop, but does not support bulk operations.
     */
    @GetMapping(value = "ServiceProviderConfig")
    public ResponseEntity<InputStreamResource> getServiceProviderConfig() throws IOException {
        return getResourceJsonFile("/scim-json/ServiceProviderConfig.json");
    }//getServiceProviderConfig

    /**
     * Return the Resource Types supported by ICE Research (User and Group)
     */
    @GetMapping(value = "ResourceTypes")
    public ResponseEntity<InputStreamResource> getResourceTypes() throws IOException {
        return getResourceJsonFile("/scim-json/ResourceTypes.json");
    }//getResourceTypes

    /**
     * Return the SCIM schemas supported by ICE Research
     */
    @GetMapping(value = "Schemas")
    public ResponseEntity<InputStreamResource> getSchemas() throws IOException {
        return getResourceJsonFile("/scim-json/Schemas.json");
    }//getSchemas

    /**
     * Handle exceptions in SCIM format
     */
    @ExceptionHandler(Exception.class)
    public ScimExceptionResponse handleException(Exception e, HttpServletResponse response) {
        HttpStatus responseStatus = HttpStatus.NOT_ACCEPTABLE;
        if (e instanceof HttpStatusCodeException) {
            responseStatus = ((HttpStatusCodeException) e).getStatusCode();
        }
        response.setStatus(responseStatus.value());
        return new ScimExceptionResponse(e.getMessage(), responseStatus.toString());
    }//handleException

    /**
     * Helper method that read JSON files for the configuration endpoints
     */
    private ResponseEntity<InputStreamResource> getResourceJsonFile(String fileName) throws IOException {
        ClassPathResource jsonFile = new ClassPathResource(fileName);

        return ResponseEntity
                .ok()
                .contentLength(jsonFile.contentLength())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new InputStreamResource(jsonFile.getInputStream()));
    }//getResourceJsonFile
}

