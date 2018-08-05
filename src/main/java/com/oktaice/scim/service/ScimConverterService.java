package com.oktaice.scim.service;

import com.oktaice.scim.model.ScimOktaIceUser;
import com.oktaice.scim.model.ScimResource;
import com.oktaice.scim.model.User;

import java.util.Map;

public interface ScimConverterService {

    String USERS_LOCATION_BASE = "/scim/v2/Users";

    ScimResource mapToScimResource(Map<String, Object> scimRequest);
    ScimOktaIceUser userToScimOktaIceUser(User user);
}
