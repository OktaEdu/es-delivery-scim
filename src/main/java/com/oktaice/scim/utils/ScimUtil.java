package com.oktaice.scim.utils;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScimUtil {

    //STATIC ATTRIBUTES: USERS
    public static final String USER_ID = "id";
    public static final String USER_UUID = "id";
    public static final String USER_FIRST_NAME = "givenName";
    public static final String USER_MIDDLE_NAME = "middleName";
    public static final String USER_LAST_NAME = "familyName";
    public static final String USER_USERNAME = "userName";
    public static final String USER_EMAIL = "email";
    public static final String USER_ICECREAM = "iceCream";
    public static final String USER_EMPNO = "employeeNumber";
    public static final String USER_CC = "costCenter";
    public static final String USER_ACTIVE = "active";

    //STATIC ATTRIBUTES: GROUPS
    public static final String GROUP_ID = "id";
    public static final String GROUP_UUID = "id";
    public static final String GROUP_NAME = "displayName";
    public static final String GROUP_MEMBERS = "members";

    //SCIM: STATIC SUPPORTING ATTRIBUTES: USERS
    public static final String USER_NAME_MAP = "name";
    public static final String USER_EMAIL_MAP = "emails";
    public static final String USER_EMAIL_MAP_TYPE = "type";
    public static final String USER_EMAIL_MAP_TYPE_WORK = "work";
    public static final String USER_EMAIL_MAP_VALUE = "value";
    public static final String USER_EMAIL_MAP_PRIMARY = "primary";
    public static final String USER_GROUPS_MAP = "groups";
    public static final String USER_GROUPS_VALUE = "value";
    public static final String USER_GROUPS_DISPLAY = "display";

    //SCIM: STATIC SUPPORTING ATTRIBUTES: GROUPS
    public static final String GROUP_MEMBERS_VALUE = "value";
    public static final String GROUP_MEMBERS_DISPLAY = "display";

    //SCIM: STATIC SUPPORTING ATTRIBUTES: SCHEMAS
    public static final String SCHEMA_ATTRIBUTE = "schemas";
    public static final String SCHEMA_USER_CORE = "urn:ietf:params:scim:schemas:core:2.0:User";
    public static final String SCHEMA_USER_ENTERPRISE = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";
    public static final String SCHEMA_GROUP_CORE = "urn:ietf:params:scim:schemas:core:2.0:Group";
    public static final String SCHEMA_LIST_RESPONSE = "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    public static final String SCHEMA_PATCHOP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    public static final String SCHEMA_USER_OKTAICE = "urn:ietf:params:scim:schemas:extension:ice:2.0:User";

    //SCIM: STATIC SUPPORTING ATTRIBUTES: LIST RESPONSES
    public static final String LIST_RESPONSE_COUNT = "count";
    public static final String LIST_RESPONSE_RESULTS = "totalResults";
    public static final String LIST_RESPONSE_INDEX = "startIndex";
    public static final String LIST_RESPONSE_SEARCH_FILTER = "filter";
    public static final String LIST_RESPONSE_ITEMS_PER_PAGE = "itemsPerPage";
    public static final String LIST_RESPONSE_RESOURCE_PLACEHOLDER = "Resources";

    //SCIM: STATIC SUPPORTING ATTRIBUTES: PATCHOP
    public static final String PATCHOP_PLACEHOLDER = "Operations";

    //SCIM: STATIC SUPPORTING ATTRIBUTES: METADATA
    public static final String META_ATTRIBUTE = "meta";
    public static final String META_RESOURCE_TYPE = "resourceType";
    public static final String META_RESOURCE_TYPE_USER = "User";
    public static final String META_RESOURCE_TYPE_GROUP = "Group";
    public static final String META_LOCATION = "location";

    //BEGIN: METHODS THAT CONVERT PAYLOAD TO JAVA OBJECT
    /**
     * Convert SCIM payload to User
     */
    public static User toUser(Map<String, Object> payload){
        //GET FLAT ATTRIBUTES


        //GET NAME ATTRIBUTES


        //GET EMAIL ATTRIBUTE


        //GET ENTERPRISE ATTRIBUTES


        //GET OKTAICE ATTRIBUTES


        return null;
    }//toUser

    /**
     * Convert SCIM payload to Group
     */
    public static Group toGroup(Map<String, Object> payload, UserRepository userRepository){
        //GET FLAT ATTRIBUTES

        //ITERATE THROUGH GROUP MEMBERS

        return null;
    }//toGroup

    //END: METHODS THAT CONVERT PAYLOAD TO JAVA OBJECT

    //BEGIN: METHODS THAT CONVERT JAVA OBJECT TO PAYLOAD

    /**
     * Convert User to SCIM
     */
    public static Map<String, Object> userToPayload(User user){
        Map<String, Object> returnValue = new HashMap<>();
        //ADD SCIM SCHEMA


        //SET FLAT ATTRIBUTES


        //SET NAME ATTRIBUTES


        //SET EMAIL ATTRIBUTE


        //SET ENTERPRISE ATTRIBUTES


        //SET OKTAICE ATTRIBUTES


        //SET GROUP MEMBERSHIP


        //SET METADATA


        return returnValue;
    }//userToPayload

    /**
     * Convert List of Users to SCIM
     */
    public static Map<String, Object> usersToPayload(List<User> users, Optional<Integer> startIndex,
                                                     Optional<Integer> pageCount){
        //GET A LIST RESPONSE


        //ITERATE LIST OF USERS

        return null;
    }//usersToPayload

    /**
     * Convert Group to SCIM
     */
    public static Map<String, Object> groupToPayload(Group group){
        Map<String, Object> returnValue = new HashMap<>();
        //ADD SCIM SCHEMA
        String[] schemas = {SCHEMA_GROUP_CORE};
        returnValue.put(SCHEMA_ATTRIBUTE, schemas);

        //SET FLAT ATTRIBUTES
        returnValue.put(GROUP_UUID, group.getUuid());
        returnValue.put(GROUP_NAME, group.getDisplayName());

        //SET GROUP MEMBERS
        if(group.getUsers() != null && group.getUsers().size() > 0) {
            List<Map> members = new ArrayList<>();
            for (User u : group.getUsers()) {
                Map<String, Object> member = new HashMap<>();
                member.put(GROUP_MEMBERS_VALUE, u.getUuid());
                member.put(GROUP_MEMBERS_DISPLAY, u.getUserName());
                members.add(member);
            }
            returnValue.put(GROUP_MEMBERS, members);
        }

        //SET METADATA
        Map<String, Object> meta = new HashMap<>();
        meta.put(META_RESOURCE_TYPE, META_RESOURCE_TYPE_GROUP);
        meta.put(META_ATTRIBUTE, ("/scim/v2/Groups/" + group.getUuid()));
        returnValue.put(META_ATTRIBUTE, meta);

        return returnValue;
    }//groupToPayload

    /**
     * Get list of Groups in SCIM ListResponse format
     */
    public static Map<String, Object> groupsToPayload(List<Group> groups, Optional<Integer> startIndex,
                                                     Optional<Integer> pageCount){
        //GET A LIST RESPONSE
        int totalResults = groups.size();
        Map<String, Object> returnValue = getListResponse(totalResults, startIndex, pageCount);

        //ITERATE LIST OF GROUPS
        List<Map> groupList = new ArrayList<>();
        for(Group g : groups){
            //CONVERT EACH GROUP AND ADD TO PAYLOAD
            groupList.add(ScimUtil.groupToPayload(g));
        }
        returnValue.put(LIST_RESPONSE_RESOURCE_PLACEHOLDER, groupList);

        return returnValue;
    }//groupsToPayload

    //END: METHODS THAT CONVERT JAVA OBJECT TO PAYLOAD

    //BEGIN: METHODS THAT UPDATE JAVA OBJECT WITH PAYLOAD DATA

    /**
     * Update User with the data passed via SCIM object
     */
    public static User updateUser(Map<String, Object> payload, User user){
        //GET FLAT ATTRIBUTES
        user.setUserName((payload.get(USER_USERNAME) != null) ? payload.get(USER_USERNAME).toString() : null);
        user.setFavoriteIceCream((payload.get(USER_ICECREAM) != null) ? payload.get(USER_ICECREAM).toString() : null);
        user.setActive((payload.get(USER_ACTIVE) != null) ? (Boolean)payload.get(USER_ACTIVE) : true);

        //GET NAME ATTRIBUTES
        Map<String, Object> name = (Map<String, Object>)payload.get(USER_NAME_MAP);
        if (name != null) {
            user.setFirstName((name.get(USER_FIRST_NAME) != null) ? name.get(USER_FIRST_NAME).toString() : null);
            user.setMiddleName((name.get(USER_MIDDLE_NAME) != null) ? name.get(USER_MIDDLE_NAME).toString() : null);
            user.setLastName((name.get(USER_LAST_NAME) != null) ? name.get(USER_LAST_NAME).toString() : null);
        }

        //GET EMAIL ATTRIBUTE
        String email = null;
        List<Map> emails = (ArrayList<Map>)payload.get(USER_EMAIL_MAP);
        if(emails != null && emails.size() > 0){
            for(Map mail : emails){
                String type = (mail.get(USER_EMAIL_MAP_TYPE) != null) ? mail.get(USER_EMAIL_MAP_TYPE).toString() : null;
                if(type.equals(USER_EMAIL_MAP_TYPE_WORK)){
                    user.setEmail((mail.get(USER_EMAIL_MAP_VALUE) != null) ? mail.get(USER_EMAIL_MAP_VALUE).toString() : null);
                }
            }
        }

        //GET ENTERPRISE ATTRIBUTES
        Map<String, Object> enterpriseAttrs = (Map<String, Object>)payload.get(SCHEMA_USER_ENTERPRISE);
        if(enterpriseAttrs != null) {
            user.setEmployeeNumber((enterpriseAttrs.get(USER_EMPNO) != null) ? enterpriseAttrs.get(USER_EMPNO).toString() : null);
            user.setCostCenter((enterpriseAttrs.get(USER_CC) != null) ? enterpriseAttrs.get(USER_CC).toString() : null);
        }

        //GET OKTAICE ATTRIBUTES
        String iceCream = null;
        Map<String, Object> iceAttrs = (Map<String, Object>)payload.get(SCHEMA_USER_OKTAICE);
        if(iceAttrs != null) {
            user.setFavoriteIceCream((iceAttrs.get(USER_ICECREAM) != null) ? iceAttrs.get(USER_ICECREAM).toString() : null);
        }

        return user;
    }//updateUser

    /**
     * Update Group with the data passed via JSON object
     */
    public static Group updateGroup(Map<String, Object> payload, Group group, UserRepository userRepository){
        //GET FLAT ATTRIBUTES
        group.setDisplayName((payload.get(GROUP_NAME) != null) ? payload.get(GROUP_NAME).toString() : null);

        //VALIDATE AND GET MEMBERS
        if (payload.get(GROUP_MEMBERS) != null) {
            List<User> members = new ArrayList();
            List<Map> ms = (ArrayList<Map>) payload.get(GROUP_MEMBERS);
            if (ms != null && ms.size() > 0) {
                for (Map member : ms) {
                    if (member.get(GROUP_MEMBERS_VALUE) != null) {
                        User u = userRepository.findOneByUuid(member.get(GROUP_MEMBERS_VALUE).toString());
                        if (u != null) {
                            members.add(u);
                        }
                    }
                }
            }
            group.setUsers(members);
        }else{
            group.setUsers(null);
        }
        return group;
    }//updateGroup

    //END: METHODS THAT UPDATE JAVA OBJECT WITH PAYLOAD DATA

    //BEGIN: SUPPORTING METHODS REQUIRED BY SCIM

    /**
     * Parse SCIM search filters
     */
    public static Matcher parseFilter(String filter){
        String regex = "(\\w+) eq \"([^\"]*)\"";
        Pattern response = Pattern.compile(regex);
        return response.matcher(filter);
    }//parseFilter

    /**
     * Get a List Response Wrapper for multi-valued searches
     */
    private static Map<String, Object> getListResponse(int totalResults, Optional<Integer> startIndex, Optional<Integer> pageCount){
        return null;
    }//getListResponse

    /**
     * Validate Patch operations
     * @throws RuntimeException in case the PatchOp is invalid
     */
    public static void validatePatchOp(@RequestBody Map<String, Object> payload) throws RuntimeException{

    }//validatePatchOp

}
