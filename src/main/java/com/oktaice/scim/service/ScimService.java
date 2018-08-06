package com.oktaice.scim.service;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimGroup;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.ScimOktaIceUser;
import com.oktaice.scim.model.ScimPatchOp;
import com.oktaice.scim.model.ScimUser;
import com.oktaice.scim.model.User;

import java.util.List;
import java.util.Map;

public interface ScimService {

    String USERS_LOCATION_BASE = "/scim/v2/Users";

    void validatePatchOp(ScimPatchOp scimPatchOp);

    ScimUser mapToScimUser(Map<String, Object> scimRequest);
    User scimUserToUser(ScimUser scimUser);
    ScimOktaIceUser userToScimOktaIceUser(User user);
    ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount);

    Group scimGroupToGroup(ScimGroup scimGroup);
    ScimGroup groupToScimGroup(Group group);
    ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount);
}
