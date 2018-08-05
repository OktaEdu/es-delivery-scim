package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScimListResponse extends ScimResource {

    public static final String SCHEMA_LIST_RESPONSE = "urn:ietf:params:scim:api:messages:2.0:ListResponse";

    private Integer totalResults;
    private Integer startIndex;
    private Integer itemsPerPage;

    @JsonProperty("Resources")
    private List<ScimResource> resources;

    public ScimListResponse() {
        getSchemas().add(SCHEMA_LIST_RESPONSE);
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<ScimResource> getResources() {
        return resources;
    }

    public void setResources(List<ScimResource> resources) {
        this.resources = resources;
    }

    public void addResource(ScimResource scimResource) {
        if (scimResource != null) {
            if (resources == null) {
                resources = new ArrayList<>();
            }
            resources.add(scimResource);
        }
    }
}
