package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * REST API for User management
 */
@RestController
@RequestMapping("/api/v1/users")
public class JsonUserController {

    UserRepository userRepository;
    GroupRepository groupRepository;

    public JsonUserController(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Return all users
     */
    @GetMapping
    public Map<String,Object> getUsers(){
        return JsonUtil.usersToPayload(userRepository.findAll());
    }//getUsers

    /**
     * Create a user
     */
    @PostMapping
    public Map createUser(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        User newUser = JsonUtil.toUser(params);
        userRepository.save(newUser);
        response.setStatus(HttpStatus.CREATED.value());//201
        return JsonUtil.userToPayload(newUser);
    }//createUser

    /**
     * Get specific user
     * @param id User login
     */
    @GetMapping("/{id}")
    public Map getUser(@PathVariable int id, HttpServletResponse response) {
        User u = userRepository.findOne(id);
        response.setStatus((u != null) ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value());//200 or 404
        return JsonUtil.userToPayload(u);
    }//getUser

    /**
     * Replace user via put
     * @param id User login
     */
    @PutMapping("/{id}")
    public @ResponseBody Map replaceUser(@RequestBody Map<String, Object> payload,
                                         @PathVariable int id,
                                         HttpServletResponse response) {
        User user = userRepository.findOne(id);
        if(user != null){
            user = JsonUtil.updateUser(payload, user);
            userRepository.save(user);
            response.setStatus(HttpStatus.OK.value());
            return JsonUtil.userToPayload(user);
        }else{
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
    }//replaceUser

    /**
     * Update user via Patch
     * @param id User login
     */
    @PatchMapping("/{id}")
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> payload,
                                          @PathVariable int id,
                                          HttpServletResponse response) {
        return this.replaceUser(payload,id,response);
    }//updateUser


    /**
     * Delete the user
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id, HttpServletResponse response) {
        User u = userRepository.findOne(id);
        if(u != null){
            //remove user from groups
            for(Group g: u.getGroups()){
                g.getUsers().remove(u);
            }
            //delete user
            userRepository.delete(u);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }else{
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }//deleteUser

}

