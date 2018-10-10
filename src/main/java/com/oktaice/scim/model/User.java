package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oktaice.scim.utils.JsonUtil;

import javax.persistence.*;
import java.util.*;

/**
 * User Entity that handles User operations in database
 */
@Entity
@Table(name="users")
public class User {

    //BEGIN: USER ATTRIBUTES
    private int id;
    private String uuid;
    private Boolean active = false;
    private String userName;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String favoriteIceCream;
    private String employeeNumber;
    private String costCenter;
    private List<Group> groups = new ArrayList<>();
    //END: USER ATTRIBUTES

    //BEGIN: CONSTRUCTORS
    public User(){
        this.uuid = UUID.randomUUID().toString();
    }

    public User(String userName, String firstName, String middleName, String lastName,
                String email, String favoriteIceCream,
                String employeeNumber, String costCenter, boolean active){
        this.uuid = UUID.randomUUID().toString();
        this.userName = (userName != null) ? userName : (firstName+"."+lastName+"@oktaice.com").toLowerCase();
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.favoriteIceCream = favoriteIceCream;
        this.employeeNumber = employeeNumber;
        this.costCenter = costCenter;
        this.active = active;
    }
    //END: CONSTRUCTORS

    //BEGIN: SUPPORTING METHODS



    @Override
    public String toString() {
        return "username: "+this.userName+ " | email: " +this.email;
    }//toString

    /**
     * For unit tests
     */
    public static void main(String args[]){
        User u = new User(null,"John", null, "Doe", "john.doe@oktaice.com", "Vanilla", "123", "C123", true);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(JsonUtil.userToPayload(u));
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }//main
    //END: SUPPORTING METHODS

    //BEGIN: GETTERS AND SETTERS

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(unique=true)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(unique=true)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(unique=true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavoriteIceCream() {
        return favoriteIceCream;
    }

    public void setFavoriteIceCream(String favoriteIceCream) {
        this.favoriteIceCream = favoriteIceCream;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
    //END: GETTERS AND SETTERS
}
