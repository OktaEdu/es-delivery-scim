package com.oktaice.scim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oktaice.scim.model.ScimEnterpriseUser;
import com.oktaice.scim.model.ScimOktaIceUser;
import com.oktaice.scim.model.ScimResource;
import com.oktaice.scim.model.ScimUser;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.oktaice.scim.model.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE;
import static com.oktaice.scim.model.ScimUser.SCHEMA_USER_CORE;
import static com.oktaice.scim.model.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScimUserTest {

    private static final String ID = "abcd1234";
    private static final String USER_NAME = "me@you.com";
    private static final boolean ACTIVE = true;
    private static final String GIVEN_NAME = "Micah";
    private static final String MIDDLE_NAME = "Philip";
    private static final String FAMILY_NAME = "Silverman";
    private static final boolean EMAIL_PRIMARY = true;
    private static final String EMAIL_ADDRESS = "micah.silverman@okta.com";
    private static final String EMAIL_TYPE = "work";
    private static final String COST_CENTER = "CC";
    private static final String EMPLOYEE_NUMBER = "EN";
    private static final String ICE_CREAM = "vanilla";
    private static final String META_RESOURCE_TYPE = "User";
    private static final String META_LOCATION_PATH = "/scim/v2/Users";

    private static final String USER_CORE = 
        (
            "`id`:`" + ID + "`,`active`:" + ACTIVE + ",`userName`:`" + USER_NAME + "`," +
            "`name`:{`givenName`:`" + GIVEN_NAME + "`,`middleName`:`" + MIDDLE_NAME + "`," + 
            "`familyName`:`" + FAMILY_NAME + "`}," + 
            "`emails`:[{`primary`:" + EMAIL_PRIMARY + ",`value`:`" + EMAIL_ADDRESS + "`,`type`:`" + EMAIL_TYPE + "`}]"
        ).replace('`', '"');

    private static final String USER_ENTERPRISE = 
        ( 
            "`" + SCHEMA_USER_ENTERPRISE + "`:" + 
            "{`employeeNumber`:`" + EMPLOYEE_NUMBER + "`,`costCenter`:`" + COST_CENTER + "`}"
        ).replace('`', '"');

    private static final String USER_OKTA_ICE = 
        (
            "`" + SCHEMA_USER_OKTA_ICE + "`:{`iceCream`:`" + ICE_CREAM + "`}"
        ).replace('`', '"');

    private static final String USER_META = 
        (
           "`meta`:{`resourceType`:`" + META_RESOURCE_TYPE + "`,`location`:`" + META_LOCATION_PATH + "/" + ID + "`}"
        ).replace('`', '"');

    private static final String SCHEMAS_START = "`schemas`:[".replace('`', '"');
    private static final String SCHEMAS_END = "],";

    private static final String SCIM_USER =
        (
            "{" + SCHEMAS_START + "`" + SCHEMA_USER_CORE + "`" + SCHEMAS_END + USER_CORE + "," + USER_META + "}"
        ).replace('`', '"');

    private static final String SCIM_ENTERPRISE_USER =
        (
            "{" + SCHEMAS_START + "`" + SCHEMA_USER_CORE + "`,`" + SCHEMA_USER_ENTERPRISE + "`" + SCHEMAS_END +
            USER_CORE + "," + USER_ENTERPRISE + "," + USER_META + "}"
        ).replace('`', '"');

    private static final String SCIM_OKTA_ICE_USER =
        (
            "{" + SCHEMAS_START + "`" +
            SCHEMA_USER_CORE + "`,`" + SCHEMA_USER_ENTERPRISE + "`,`" + SCHEMA_USER_OKTA_ICE +
            "`" + SCHEMAS_END + USER_CORE + "," + USER_ENTERPRISE + "," + USER_OKTA_ICE + "," + USER_META + "}"
        ).replace('`', '"');

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void userToJsonTest() throws JsonProcessingException {
        ScimUser user = new ScimUser();

        // flat attributes
        user.setId(ID);
        user.setActive(ACTIVE);
        user.setUserName(USER_NAME);

        // name
        ScimUser.Name name = new ScimUser.Name();
        user.setName(name);
        name.setGivenName(GIVEN_NAME);
        name.setMiddleName(MIDDLE_NAME);
        name.setFamilyName(FAMILY_NAME);

        // emails
        List<ScimUser.Email> emails = new ArrayList<>();
        ScimUser.Email email = new ScimUser.Email();
        emails.add(email);
        user.setEmails(emails);
        email.setPrimary(EMAIL_PRIMARY);
        email.setType(EMAIL_TYPE);
        email.setValue(EMAIL_ADDRESS);

        // meta
        ScimResource.Meta meta = new ScimResource.Meta();
        user.setMeta(meta);
        meta.setLocation(META_LOCATION_PATH + "/" + ID);
        meta.setResourceType(META_RESOURCE_TYPE);

        String actual = mapper.writeValueAsString(user);

        assertEquals(SCIM_USER, actual);
    }

    @Test
    public void enterpriseUserToJsonTest() throws JsonProcessingException {
        ScimEnterpriseUser user = new ScimEnterpriseUser();

        // flat attributes
        user.setActive(ACTIVE);
        user.setId(ID);
        user.setUserName(USER_NAME);

        // name
        ScimUser.Name name = new ScimUser.Name();
        user.setName(name);
        name.setGivenName(GIVEN_NAME);
        name.setMiddleName(MIDDLE_NAME);
        name.setFamilyName(FAMILY_NAME);

        // emails
        List<ScimUser.Email> emails = new ArrayList<>();
        ScimUser.Email email = new ScimUser.Email();
        emails.add(email);
        user.setEmails(emails);
        email.setPrimary(EMAIL_PRIMARY);
        email.setType(EMAIL_TYPE);
        email.setValue(EMAIL_ADDRESS);

        // enterprise attributes
        ScimEnterpriseUser.EnterpriseAttributes enterpriseAttributes = new ScimEnterpriseUser.EnterpriseAttributes();
        user.setEnterpriseAttributes(enterpriseAttributes);
        enterpriseAttributes.setCostCenter(COST_CENTER);
        enterpriseAttributes.setEmployeeNumber(EMPLOYEE_NUMBER);

        // meta
        ScimResource.Meta meta = new ScimResource.Meta();
        user.setMeta(meta);
        meta.setLocation(META_LOCATION_PATH + "/" + ID);
        meta.setResourceType(META_RESOURCE_TYPE);

        String actual = mapper.writeValueAsString(user);

        assertEquals(SCIM_ENTERPRISE_USER, actual);
    }

    @Test
    public void oktaIceUserToJsonTest() throws JsonProcessingException {
        ScimOktaIceUser user = new ScimOktaIceUser();

        // flat attributes
        user.setActive(ACTIVE);
        user.setId(ID);
        user.setUserName(USER_NAME);

        // name
        ScimUser.Name name = new ScimUser.Name();
        user.setName(name);
        name.setGivenName(GIVEN_NAME);
        name.setMiddleName(MIDDLE_NAME);
        name.setFamilyName(FAMILY_NAME);

        // emails
        List<ScimUser.Email> emails = new ArrayList<>();
        ScimUser.Email email = new ScimUser.Email();
        emails.add(email);
        user.setEmails(emails);
        email.setPrimary(EMAIL_PRIMARY);
        email.setType(EMAIL_TYPE);
        email.setValue(EMAIL_ADDRESS);

        // enterprise attributes
        ScimEnterpriseUser.EnterpriseAttributes enterpriseAttributes = new ScimEnterpriseUser.EnterpriseAttributes();
        user.setEnterpriseAttributes(enterpriseAttributes);
        enterpriseAttributes.setCostCenter(COST_CENTER);
        enterpriseAttributes.setEmployeeNumber(EMPLOYEE_NUMBER);

        // okta ice attributes
        ScimOktaIceUser.OktaIceAttributes oktaIceAttributes = new ScimOktaIceUser.OktaIceAttributes();
        user.setOktaIceAttributes(oktaIceAttributes);
        oktaIceAttributes.setIceCream(ICE_CREAM);

        // meta
        ScimResource.Meta meta = new ScimResource.Meta();
        user.setMeta(meta);
        meta.setLocation(META_LOCATION_PATH + "/" + ID);
        meta.setResourceType(META_RESOURCE_TYPE);

        String actual = mapper.writeValueAsString(user);

        assertEquals(SCIM_OKTA_ICE_USER, actual);
    }

    @Test
    public void JsonToOktaIceUserTest() throws IOException {
        ScimOktaIceUser user = mapper.readValue(SCIM_OKTA_ICE_USER, ScimOktaIceUser.class);

        // verify schemas
        assertTrue(user.getSchemas().contains(SCHEMA_USER_CORE));
        assertTrue(user.getSchemas().contains(SCHEMA_USER_ENTERPRISE));
        assertTrue(user.getSchemas().contains(SCHEMA_USER_OKTA_ICE));

        // verify flat attributes
        assertEquals(ACTIVE, user.isActive());
        assertEquals(USER_NAME, user.getUserName());
        assertEquals(ID, user.getId());

        // verify name
        assertEquals(GIVEN_NAME, user.getName().getGivenName());
        assertEquals(MIDDLE_NAME, user.getName().getMiddleName());
        assertEquals(FAMILY_NAME, user.getName().getFamilyName());

        // verify email
        ScimUser.Email email = user.getEmails().get(0);
        assertEquals(EMAIL_PRIMARY, email.isPrimary());
        assertEquals(EMAIL_TYPE, email.getType());
        assertEquals(EMAIL_ADDRESS, email.getValue());

        // verify enterprise
        assertEquals(COST_CENTER, user.getEnterpriseAttributes().getCostCenter());
        assertEquals(EMPLOYEE_NUMBER, user.getEnterpriseAttributes().getEmployeeNumber());

        // verify okta ice
        assertEquals(ICE_CREAM, user.getOktaIceAttributes().getIceCream());
    }

    @Test
    public void JsonToEnterpriseUserTest() throws IOException {
         ScimEnterpriseUser user = mapper.readValue(SCIM_ENTERPRISE_USER, ScimEnterpriseUser.class);

        // verify schemas
        assertTrue(user.getSchemas().contains(SCHEMA_USER_CORE));
        assertTrue(user.getSchemas().contains(SCHEMA_USER_ENTERPRISE));

        // verify flat attributes
        assertEquals(ACTIVE, user.isActive());
        assertEquals(USER_NAME, user.getUserName());
        assertEquals(ID, user.getId());

        // verify name
        assertEquals(GIVEN_NAME, user.getName().getGivenName());
        assertEquals(MIDDLE_NAME, user.getName().getMiddleName());
        assertEquals(FAMILY_NAME, user.getName().getFamilyName());

        // verify email
        ScimUser.Email email = user.getEmails().get(0);
        assertEquals(EMAIL_PRIMARY, email.isPrimary());
        assertEquals(EMAIL_TYPE, email.getType());
        assertEquals(EMAIL_ADDRESS, email.getValue());

        // verify enterprise
        assertEquals(COST_CENTER, user.getEnterpriseAttributes().getCostCenter());
        assertEquals(EMPLOYEE_NUMBER, user.getEnterpriseAttributes().getEmployeeNumber());
    }

    @Test
    public void JsonToUserTest() throws IOException {
        ScimUser user = mapper.readValue(SCIM_USER, ScimUser.class);

        // verify schemas
        assertTrue(user.getSchemas().contains(SCHEMA_USER_CORE));

        // verify flat attributes
        assertEquals(ACTIVE, user.isActive());
        assertEquals(USER_NAME, user.getUserName());
        assertEquals(ID, user.getId());

        // verify name
        assertEquals(GIVEN_NAME, user.getName().getGivenName());
        assertEquals(MIDDLE_NAME, user.getName().getMiddleName());
        assertEquals(FAMILY_NAME, user.getName().getFamilyName());

        // verify email
        ScimUser.Email email = user.getEmails().get(0);
        assertEquals(EMAIL_PRIMARY, email.isPrimary());
        assertEquals(EMAIL_TYPE, email.getType());
        assertEquals(EMAIL_ADDRESS, email.getValue());
    }
}
