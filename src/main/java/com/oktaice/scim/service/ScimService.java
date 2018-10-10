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

    /**
     * These two methods make sure ScimUserPatchOp and ScimGroupPatchOp are properly formatted.
     */
    void validateUserPatchOp(ScimUserPatchOp scimUserPatchOp);
    void validateGroupPatchOp(ScimGroupPatchOp scimGroupPatchOp);

    /**
     * These three methods focus on the User objects transformations.
     * For example, scimUserToUser (ScimUser scimUser) method takes in a ScimUser and return a traditional API user.
     * The User object is suitable for storing in the repository.
     * The userToScimOktaIceUser (User user) method serves the other way around.
     * It takes in an API user and returns a ScimOktaIceUser.
     */
    User scimUserToUser(ScimUser scimUser);
    ScimOktaIceUser userToScimOktaIceUser(User user);
    ScimListResponse usersToListResponse(List<User> users, Integer startIndex, Integer pageCount);

    /**
     * These four methods focus on the Group objects transformations.
     * For example, scimGroupToGroup (ScimGroup scimGroup) method takes in a ScimGroup
     * and return a traditional API group.
     * The groupToScimGroup (Group group) method serves the other way around.
     * It takes in an API group and returns a ScimGroup.
     */
    Group scimGroupToGroup(ScimGroup scimGroup);
    ScimGroup groupToScimGroup(Group group);
    //The updateGroupByPatchOp method update the Repository Group with the patch information
    void updateGroupByPatchOp(Group group, ScimGroupPatchOp scimGroupPatchOp);
    ScimListResponse groupsToListResponse(List<Group> groups, Integer startIndex, Integer pageCount);
}
