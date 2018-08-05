package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.oktaice.scim.model.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;

@JsonPropertyOrder({ "schemas", "id", "active", "userName", "name", "emails", "groups", SCHEMA_USER_ENTERPRISE, "meta" })
public class ScimEnterpriseUser extends ScimUser {

    public static final String SCHEMA_USER_ENTERPRISE = SCHEMA_BASE + ":extension:enterprise:2.0:User";

    @JsonProperty(SCHEMA_USER_ENTERPRISE)
    private EnterpriseAttributes enterpriseAttributes;

    public ScimEnterpriseUser() {
        super();
        getSchemas().add(SCHEMA_USER_ENTERPRISE);
    }

    public EnterpriseAttributes getEnterpriseAttributes() {
        return enterpriseAttributes;
    }

    public void setEnterpriseAttributes(EnterpriseAttributes enterpriseAttributes) {
        this.enterpriseAttributes = enterpriseAttributes;
    }

    public static class EnterpriseAttributes {

        private String employeeNumber;
        private String costCenter;

        public String getEmployeeNumber() {
            return employeeNumber;
        }

        public void setEmployeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
        }

        public String getCostCenter() {
            return costCenter;
        }

        public void setCostCenter(String costCenter) {
            this.costCenter = costCenter;
        }
    }
}
