package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

public class ScimResource {

    public static final String SCHEMA_BASE = "urn:ietf:params:scim:schemas";

    private List<String> schemas = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Meta meta;

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void addSchema(String schema) {
        schemas.add(schema);
    }

    public static class Meta {

        public static final String RESOURCE_TYPE_USER = "User";
        public static final String RESOURCE_TYPE_GROUP = "Group";

        private String resourceType;
        private String location;

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
