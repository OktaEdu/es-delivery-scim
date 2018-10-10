package com.oktaice.scim.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oktaice.scim.utils.JsonUtil;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="groups")
public class Group {

    //BEGIN: GROUP ATTRIBUTES
    private int id;
    private String uuid;
    private String displayName;
    private List<User> users = new ArrayList<>();
    //END: GROUP ATTRIBUTES

    //BEGIN: CONSTRUCTORS
    public Group(){
        this.uuid = UUID.randomUUID().toString();
    }

    public Group(String displayName, List<User> users){
        this();
        this.displayName = displayName;
        this.users = users;
    }//Group
    //END: CONSTRUCTORS

    //BEGIN: SUPPORTING METHODS
    /**
     * For unit tests
     */
    public static void main(String args[]){
        List users = new ArrayList<User>();
        users.add(new User(null,"John",null,"Lindrik","joe.lindrik@oktaice.com","Cookies'n Cream","ABC101","US_RD",true));
        users.add(new User(null,"Jane",null,"Doe","jane.doe@oktaice.com","Salted Caramel Cup","ABC102","US_RD",true));
        users.add(new User(null,"Mark",null,"Doe","mark.doe@oktaice.com","Orange Peel","ABC103","US_RD",true));
        Group g = new Group("Administrators",users);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(JsonUtil.groupToPayload(g));
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }//main
    //END: SUPPORTING METHODS

    //BEGIN: GETTERS AND SETTERS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @ManyToMany
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    //END: GETTERS AND SETTERS

}
