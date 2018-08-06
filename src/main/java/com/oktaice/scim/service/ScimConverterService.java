package com.oktaice.scim.service;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimGroup;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.ScimOktaIceUser;
import com.oktaice.scim.model.ScimUser;
import com.oktaice.scim.model.User;

import java.util.List;
import java.util.Map;

public interface ScimConverterService {

    String USERS_LOCATION_BASE = "/scim/v2/Users";

    ScimUser mapToScimUser(Map<String, Object> scimRequest);
    User scimUserToUser(ScimUser scimUser);

    ScimOktaIceUser userToScimOktaIceUser(User user);
    ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount);

    ScimGroup groupToScimGroup(Group group);
    ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount);
}
