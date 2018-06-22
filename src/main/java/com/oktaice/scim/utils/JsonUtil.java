package com.oktaice.scim.utils;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    //STATIC ATTRIBUTES: USERS
    public static final String USER_ID = "id";
    public static final String USER_UUID = "uuid";
    public static final String USER_FIRST_NAME = "first";
    public static final String USER_MIDDLE_NAME = "middle";
    public static final String USER_LAST_NAME = "last";
    public static final String USER_USERNAME = "login";
    public static final String USER_EMAIL = "email";
    public static final String USER_ICECREAM = "iceCream";
    public static final String USER_EMPNO = "employeeNumber";
    public static final String USER_CC = "costCenter";
    public static final String USER_ACTIVE = "isActive";

    //STATIC ATTRIBUTES: GROUPS
    public static final String GROUP_ID = "id";
    public static final String GROUP_UUID = "uuid";
    public static final String GROUP_NAME = "name";
    public static final String GROUP_MEMBERS = "members";

    //BEGIN: METHODS THAT CONVERT PAYLOAD TO JAVA OBJECT
    /**
     * Convert JSON payload to User
     */
    public static User toUser(Map<String, Object> payload){
        //GET FLAT ATTRIBUTES
        String userName = (payload.get(USER_USERNAME) != null) ? payload.get(USER_USERNAME).toString() : null;
        String firstName = (payload.get(USER_FIRST_NAME) != null) ? payload.get(USER_FIRST_NAME).toString() : null;
        String middleName = (payload.get(USER_MIDDLE_NAME) != null) ? payload.get(USER_MIDDLE_NAME).toString() : null;
        String lastName = (payload.get(USER_LAST_NAME) != null) ? payload.get(USER_LAST_NAME).toString() : null;
        String email = (payload.get(USER_EMAIL) != null) ? payload.get(USER_EMAIL).toString() : null;
        String iceCream = (payload.get(USER_ICECREAM) != null) ? payload.get(USER_ICECREAM).toString() : null;
        String empNo = (payload.get(USER_EMPNO) != null) ? payload.get(USER_EMPNO).toString() : null;
        String cc = (payload.get(USER_CC) != null) ? payload.get(USER_CC).toString() : null;
        Boolean active = (payload.get(USER_ACTIVE) != null) ? (Boolean)payload.get(USER_ACTIVE) : true ;
        return new User(userName, firstName, middleName, lastName, email, iceCream, empNo, cc, active);
    }//toUser

    /**
     * Convert JSON payload to Group
     */
    public static Group toGroup(Map<String, Object> payload, UserRepository userRepository){
        //GET FLAT ATTRIBUTES
        String displayName = payload.get(GROUP_NAME).toString();
        //ITERATE THROUGH GROUP MEMBERS
        List<User> members = new ArrayList();
        List<Integer> ms = (List<Integer>)payload.get(GROUP_MEMBERS);
        for( int m : ms){
            User u = userRepository.findOne(m);
            if(u == null){
                System.out.println("User does not exists");
            }else{
                members.add(u);
            }
        }
        return new Group(displayName,members);
    }//toGroup

    //END: METHODS THAT CONVERT PAYLOAD TO JAVA OBJECT

    //BEGIN: METHODS THAT CONVERT JAVA OBJECT TO PAYLOAD

    /**
     * Convert User to JSON
     */
    public static Map<String, Object> userToPayload(User user){
        //SET FLAT ATTRIBUTES
        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put(USER_ID, Integer.toString(user.getId()));
        returnValue.put(USER_UUID, user.getUuid());
        returnValue.put(USER_ACTIVE, user.getActive());
        returnValue.put(USER_USERNAME, user.getUserName());
        returnValue.put(USER_FIRST_NAME, user.getFirstName());
        returnValue.put(USER_MIDDLE_NAME, user.getMiddleName());
        returnValue.put(USER_LAST_NAME, user.getLastName());
        returnValue.put(USER_EMPNO, user.getEmployeeNumber());
        returnValue.put(USER_CC, user.getCostCenter());
        returnValue.put(USER_ICECREAM, user.getFavoriteIceCream());
        return returnValue;
    }//userToPayload

    /**
     * Convert List of Users to JSON
     */
    public static Map<String, Object> usersToPayload(List<User> users){
        Map<String, Object> returnValue = new HashMap<>();

        //ITERATE LIST OF USERS
        List<Map> usr = new ArrayList<>();
        for(User u : users){
            //CONVERT EACH USER AND ADD TO PAYLOAD
            usr.add(userToPayload(u));
        }
        returnValue.put("users", usr);

        return returnValue;
    }//usersToPayload

    /**
     * Convert Group to JSON
     */
    public static Map<String, Object> groupToPayload(Group group){
        Map<String, Object> returnValue = new HashMap<>();

        //SET FLAT ATTRIBUTES
        returnValue.put(GROUP_ID, group.getId());
        returnValue.put(GROUP_NAME, group.getDisplayName());
        returnValue.put(GROUP_UUID, group.getUuid());

        //SET GROUP MEMBERS
        List<Integer> members = new ArrayList();
        for(User u : group.getUsers()){
            members.add(u.getId());
        }
        returnValue.put(GROUP_MEMBERS, members);

        return returnValue;
    }//groupToPayload

    /**
     * Convert List of Groups to JSON
     */
    public static Map<String, Object> groupsToPayload(List<Group> groups){
        Map<String, Object> returnValue = new HashMap<>();

        //ITERATE LIST OF GROUPS
        List<Map> groupList = new ArrayList<>();
        for(Group g : groups){
            //CONVERT EACH GROUP AND ADD TO PAYLOAD
            groupList.add(JsonUtil.groupToPayload(g));
        }
        returnValue.put("groups", groupList);
        return returnValue;
    }//groupsToPayload

    //END: METHODS THAT CONVERT JAVA OBJECT TO PAYLOAD

    //BEGIN: METHODS THAT UPDATE JAVA OBJECT WITH PAYLOAD DATA

    /**
     * Update User with the data passed via JSON object
     */
    public static User updateUser(Map<String, Object> payload, User user){
        //GET FLAT ATTRIBUTES
        if(payload.get(USER_USERNAME) != null){
            user.setUserName(payload.get(USER_USERNAME).toString());
        }
        if(payload.get(USER_ICECREAM) != null){
            user.setFavoriteIceCream(payload.get(USER_ICECREAM).toString());
        }
        if(payload.get(USER_ACTIVE) != null){
            user.setActive((Boolean)payload.get(USER_ACTIVE));
        }
        if(payload.get(USER_FIRST_NAME) != null){
            user.setFirstName(payload.get(USER_FIRST_NAME).toString());
        }
        if(payload.get(USER_MIDDLE_NAME) != null){
            user.setMiddleName(payload.get(USER_MIDDLE_NAME).toString());
        }
        if(payload.get(USER_LAST_NAME) != null){
            user.setLastName(payload.get(USER_LAST_NAME).toString());
        }
        if(payload.get(USER_EMAIL) != null){
            user.setEmail(payload.get(USER_EMAIL).toString());
        }
        if(payload.get(USER_EMPNO) != null){
            user.setEmployeeNumber(payload.get(USER_EMPNO).toString());
        }
        if(payload.get(USER_CC) != null){
            user.setCostCenter(payload.get(USER_CC).toString());
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
        if (payload.get(GROUP_MEMBERS) != null){
            List<User> members = new ArrayList<>();
            List<Integer> ms = (ArrayList<Integer>)payload.get(GROUP_MEMBERS);
            for(Integer m : ms){
                User u = userRepository.findOne(m);
                members.add(u);
            }
            group.setUsers(members);
        }
        return group;
    }//updateGroup

    //END: METHODS THAT UPDATE JAVA OBJECT WITH PAYLOAD DATA

}
