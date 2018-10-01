package com.oktaice.scim.model.scim.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.oktaice.scim.model.scim.ScimEnterpriseUser;
import com.oktaice.scim.model.scim.ScimOktaIceUser;
import com.oktaice.scim.model.scim.ScimUser;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

import static com.oktaice.scim.model.scim.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;
import static com.oktaice.scim.model.scim.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE;
import static com.oktaice.scim.model.scim.ScimUser.SCHEMA_USER_CORE;

@JsonComponent
public class ScimUserDeserializer extends JsonDeserializer<ScimUser> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ScimUser deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {

        boolean isScimUser = false;
        boolean isScimEnterpriserUser = false;
        boolean isScimOktaIceUser = false;

        // determine type by present schema(s)
        TreeNode tree = parser.readValueAsTree();

        for (JsonNode node : (ArrayNode) tree.get("schemas")) {
            String schema = node.asText();
            switch (schema) {
                case SCHEMA_USER_CORE:
                    isScimUser = true;
                    break;
                case SCHEMA_USER_ENTERPRISE:
                    isScimEnterpriserUser = true;
                    break;
                case SCHEMA_USER_OKTA_ICE:
                    isScimOktaIceUser = true;
                default:
            }
        }
        if (isScimUser && isScimEnterpriserUser && isScimOktaIceUser) {
            return mapper.readValue(tree.toString(), ScimOktaIceUser.class);
        } else if (isScimUser && isScimOktaIceUser) {
            return mapper.readValue(tree.toString(), ScimOktaIceUser.class);
        } else if (isScimUser && isScimEnterpriserUser) {
            return mapper.readValue(tree.toString(), ScimEnterpriseUser.class);
        } else if (isScimUser) {
            return mapper.readValue(tree.toString(), ScimUser.class);
        }
        throw new RuntimeException("SCIM Resource not supported");
    }
}
