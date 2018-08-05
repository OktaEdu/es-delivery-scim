package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.oktaice.scim.model.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;
import static com.oktaice.scim.model.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE;

@JsonPropertyOrder({ "schemas", "id", "active", "userName", "name", "emails", "groups", SCHEMA_USER_ENTERPRISE, SCHEMA_USER_OKTA_ICE, "meta" })
public class ScimOktaIceUser extends ScimEnterpriseUser {

    public static final String SCHEMA_USER_OKTA_ICE = SCHEMA_BASE + ":extension:ice:2.0:User";

    @JsonProperty(SCHEMA_USER_OKTA_ICE)
    private OktaIceAttributes oktaIceAttributes;

    public ScimOktaIceUser() {
        super();
        getSchemas().add(SCHEMA_USER_OKTA_ICE);
    }

    public OktaIceAttributes getOktaIceAttributes() {
        return oktaIceAttributes;
    }

    public void setOktaIceAttributes(OktaIceAttributes oktaIceAttributes) {
        this.oktaIceAttributes = oktaIceAttributes;
    }

    public static class OktaIceAttributes {

        private String iceCream;

        public String getIceCream() {
            return iceCream;
        }

        public void setIceCream(String iceCream) {
            this.iceCream = iceCream;
        }
    }
}
