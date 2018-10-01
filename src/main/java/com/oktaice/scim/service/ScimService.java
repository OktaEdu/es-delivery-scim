package com.oktaice.scim.service;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim.ScimGroup;
import com.oktaice.scim.model.scim.ScimGroupPatchOp;
import com.oktaice.scim.model.scim.ScimListResponse;
import com.oktaice.scim.model.scim.ScimOktaIceUser;
import com.oktaice.scim.model.scim.ScimUser;
import com.oktaice.scim.model.scim.ScimUserPatchOp;
import com.oktaice.scim.model.User;

import java.util.List;

public interface ScimService {

    String USERS_LOCATION_BASE = "/scim/v2/Users";
    String GROUPS_LOCATION_BASE = "/scim/v2/Groups";

    void validateUserPatchOp(ScimUserPatchOp scimUserPatchOp);
    void validateGroupPatchOp(ScimGroupPatchOp scimGroupPatchOp);

    User scimUserToUser(ScimUser scimUser);
    ScimOktaIceUser userToScimOktaIceUser(User user);
    ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount);

    Group scimGroupToGroup(ScimGroup scimGroup);
    ScimGroup groupToScimGroup(Group group);
    void updateGroupByPatchOp(Group group, ScimGroupPatchOp scimGroupPatchOp);
    ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount);
}
