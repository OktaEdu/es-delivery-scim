package com.oktaice.scim.model.scim;

public class ScimExceptionResponse extends ScimResource {

    public final static String ERROR_SCHEMA = "urn:ietf:params:scim:api:messages:2.0:Error";

    private String detail;
    private String status;

    public ScimExceptionResponse() {
        super();
        getSchemas().add(ERROR_SCHEMA);
    }

    public ScimExceptionResponse(String detail, String status) {
        this();
        this.detail = detail;
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
