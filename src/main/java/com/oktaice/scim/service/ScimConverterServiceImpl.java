package com.oktaice.scim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimEnterpriseUser;
import com.oktaice.scim.model.ScimExceptionResponse;
import com.oktaice.scim.model.ScimGroup;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.ScimOktaIceUser;
import com.oktaice.scim.model.ScimResource;
import com.oktaice.scim.model.ScimUser;
import com.oktaice.scim.model.User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oktaice.scim.model.ScimEnterpriseUser.SCHEMA_USER_ENTERPRISE;
import static com.oktaice.scim.model.ScimOktaIceUser.SCHEMA_USER_OKTA_ICE;
import static com.oktaice.scim.model.ScimUser.SCHEMA_USER_CORE;

@Service
public class ScimConverterServiceImpl implements ScimConverterService {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public ScimResource mapToScimResource(Map<String, Object> scimRequest) {
        Assert.notNull(scimRequest, "scimRequest Map must not be null");

        // this will either be a ScimUSer, ScimEnterpriseUser, ScimOktaIceUser, or ScimGroup
        // let's get the schemas to check
        List<String> schemas = (List<String>) scimRequest.get("schemas");
        boolean isScimUser, isScimEnterpriseUser, isScimOktaIceUser;
        isScimUser = isScimEnterpriseUser = isScimOktaIceUser = false;
        if (schemas != null) {
            for (String schema : schemas) {
                switch (schema) {
                    case SCHEMA_USER_CORE:
                        isScimUser = true;
                        break;
                    case SCHEMA_USER_ENTERPRISE:
                        isScimEnterpriseUser = true;
                        break;
                    case SCHEMA_USER_OKTA_ICE:
                        isScimOktaIceUser = true;
                        break;
                    default:
                }
            }
        }
        if (isScimUser && isScimEnterpriseUser && isScimOktaIceUser) {
            return mapper.convertValue(scimRequest, ScimOktaIceUser.class);
        } else if (isScimUser && isScimEnterpriseUser) {
            return mapper.convertValue(scimRequest, ScimEnterpriseUser.class);
        } else if (isScimUser) {
            return mapper.convertValue(scimRequest, ScimUser.class);
        }

        return new ScimExceptionResponse("SCIM Resource not supported", "400");
    }

    @Override
    public ScimOktaIceUser userToScimOktaIceUser(User user) {
        Assert.notNull(user, "User must not be null");

        // automatically sets schemas
        ScimOktaIceUser scimOktaIceUser = new ScimOktaIceUser();

        // flat attributes
        scimOktaIceUser.setId(user.getUuid());
        scimOktaIceUser.setUserName(user.getUserName());
        scimOktaIceUser.setActive(user.getActive());

        // name attribute
        ScimUser.Name name = new ScimUser.Name();
        name.setGivenName(user.getFirstName());
        name.setMiddleName(user.getMiddleName());
        name.setFamilyName(user.getLastName());
        scimOktaIceUser.setName(name);

        // email(s) attribute
        ScimUser.Email email = new ScimUser.Email();
        email.setPrimary(true);
        email.setType("work");
        email.setValue(user.getEmail());
        List<ScimUser.Email> emails = new ArrayList<>();
        emails.add(email);
        scimOktaIceUser.setEmails(emails);

        // group(s) attribute
        for (Group group : user.getGroups()) {
            ScimUser.Group scimUserGroup = new ScimUser.Group();
            scimUserGroup.setDisplay(group.getDisplayName());
            scimUserGroup.setValue(group.getUuid());
            scimOktaIceUser.getGroups().add(scimUserGroup);
        }

        // enterprise attributes
        ScimEnterpriseUser.EnterpriseAttributes enterpriseAttributes = new ScimEnterpriseUser.EnterpriseAttributes();
        enterpriseAttributes.setEmployeeNumber(user.getEmployeeNumber());
        enterpriseAttributes.setCostCenter(user.getCostCenter());
        scimOktaIceUser.setEnterpriseAttributes(enterpriseAttributes);

        // okta ice attributes
        ScimOktaIceUser.OktaIceAttributes oktaIceAttributes = new ScimOktaIceUser.OktaIceAttributes();
        oktaIceAttributes.setIceCream(user.getFavoriteIceCream());
        scimOktaIceUser.setOktaIceAttributes(oktaIceAttributes);

        // meta attributes
        ScimResource.Meta meta = new ScimResource.Meta();
        meta.setResourceType(ScimResource.Meta.RESOURCE_TYPE_USER);
        meta.setLocation(USERS_LOCATION_BASE + "/" + user.getUuid());
        scimOktaIceUser.setMeta(meta);

        return scimOktaIceUser;
    }

    @Override
    public ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount) {
        ScimListResponse scimListResponse = new ScimListResponse();

        scimListResponse.setStartIndex(startIndex);
        scimListResponse.setItemsPerPage(pageCount);
        scimListResponse.setTotalResults(users.size());

        for (User user : users) {
            scimListResponse.getResources().add(userToScimOktaIceUser(user));
        }

        return scimListResponse;
    }

    @Override
    public ScimGroup groupToScimGroup(Group group) {
        Assert.notNull(group, "Group must not be null");

        // automatically sets schemas
        ScimGroup scimGroup = new ScimGroup();

        // flat attributes
        scimGroup.setId(group.getUuid());
        scimGroup.setDisplayName(group.getDisplayName());

        // member attributes
        for (User user : group.getUsers()) {
            ScimGroup.Member member = new ScimGroup.Member();
            member.setValue(user.getUuid());
            member.setDisplay(user.getUserName());
            scimGroup.getMembers().add(member);
        }

        return scimGroup;
    }

    @Override
    public ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount) {
        ScimListResponse scimListResponse = new ScimListResponse();

        scimListResponse.setStartIndex(startIndex);
        scimListResponse.setItemsPerPage(pageCount);
        scimListResponse.setTotalResults(groups.size());

        for (Group group : groups) {
            scimListResponse.getResources().add(groupToScimGroup(group));
        }

        return scimListResponse;
    }
}
