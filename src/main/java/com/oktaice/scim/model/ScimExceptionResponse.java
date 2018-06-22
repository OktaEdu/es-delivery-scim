package com.oktaice.scim.model;

public class ScimExceptionResponse {

    private final String[] schemas = {"urn:ietf:params:scim:api:messages:2.0:Error"};
    private final String detail;
    private final String status;


    public ScimExceptionResponse(String detail, String status) {
        this.detail = detail;
        this.status = status;
    }

    public String[] getSchemas() {
        return schemas;
    }

    public String getDetail() {
        return detail;
    }

    public String getStatus() {
        return status;
    }
}
