package com.oktaice.scim.model.scim;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScimListResponse extends ScimResource {

    //The ScimListResponse class has its own SCIM schema
    public static final String SCHEMA_LIST_RESPONSE = "urn:ietf:params:scim:api:messages:2.0:ListResponse";

    //The ScimListResponse class contains meta information about the search that can be used for pagination.
    private Integer totalResults;
    private Integer startIndex;
    private Integer itemsPerPage;

    /**
     * The ScimListResponse serves as a wrapper for multiple SCIM Recourses.
     * For example, it can return a list of SCIM users or SCIM groups.
     */
    @JsonProperty("Resources")
    private List<ScimResource> resources = new ArrayList<>();

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
}
