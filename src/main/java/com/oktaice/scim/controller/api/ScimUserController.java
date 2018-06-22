package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.utils.ScimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/scim/v2/Users")
public class ScimUserController extends ScimBaseController {

    UserRepository userRepository;

    public ScimUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Return all users with pagination
     * @return ListResponse containing several Users
     */
    @GetMapping
    public @ResponseBody Map getUsers(@RequestParam Map<String, String> params){
        Page<User> users = null;

        //GET STARTINDEX AND COUNT FOR PAGINATION
        int count = (params.get(ScimUtil.LIST_RESPONSE_COUNT) != null) ? Integer.parseInt(params.get(ScimUtil.LIST_RESPONSE_COUNT)) : 100;
        int startIndex = (params.get(ScimUtil.LIST_RESPONSE_INDEX) != null) ? Integer.parseInt(params.get(ScimUtil.LIST_RESPONSE_INDEX)) : 1;
        startIndex = (startIndex < 1) ? 1 : startIndex;
        PageRequest pageRequest = new PageRequest((startIndex-1), count);

        //PARSE SEARCH FILTER
        String filter = params.get(ScimUtil.LIST_RESPONSE_SEARCH_FILTER);
        if (filter != null && filter.contains("eq")) {
            Matcher match = ScimUtil.parseFilter(filter);
            if (match.find()) {
                String searchKeyName = match.group(1);
                String searchValue = match.group(2);
                //IF THERE'S A VALID FILTER, USE THE PROPER METHOD FOR USER SEARCH
                switch (searchKeyName) {
                    case ScimUtil.USER_USERNAME:
                        users = userRepository.findByUsername(searchValue, pageRequest);
                        break;
                    case ScimUtil.USER_ACTIVE:
                        users = userRepository.findByActive(Boolean.valueOf(searchValue), pageRequest);
                        break;
                    case ScimUtil.USER_FIRST_NAME:
                        users = userRepository.findByFirstName(searchValue, pageRequest);
                        break;
                    case ScimUtil.USER_LAST_NAME:
                        users = userRepository.findByLastName(searchValue, pageRequest);
                        break;
                    default:
                        throw new HttpClientErrorException(HttpStatus.NOT_IMPLEMENTED, "Filter not implemented");
                }
            } else {
                //IF FILTER IS NOT RECOGNIZED, FIND ALL ENTRIES
                users = userRepository.findAll(pageRequest);
            }
        } else {
            //IF THERE'S NO FILTER, FIND ALL ENTRIES
            users = userRepository.findAll(pageRequest);
        }
        //GET LIST OF USERS FROM SEARCH AND CONVERT TO SCIM FOR RESPONSE
        List<User> foundUsers = users.getContent();
        return ScimUtil.usersToPayload(foundUsers, Optional.of(startIndex), Optional.of(count));
    }//getUsers

    /**
     * Create a user
     */
    @PostMapping
    public @ResponseBody Map createUser(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        User newUser = ScimUtil.toUser(params);
        userRepository.save(newUser);
        response.setStatus(HttpStatus.CREATED.value());//201
        return ScimUtil.userToPayload(newUser);
    }//createUser

    /**
     * Get specific user
     * @param uuid User uuid
     */
    @GetMapping("/{uuid}")
    public @ResponseBody Map getUser(@PathVariable String uuid, HttpServletResponse response) {
        User u = userRepository.findOneByUuid(uuid);
        if(u == null){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return ScimUtil.userToPayload(u);
    }//getUser

    /**
     * Replace user via put
     * @param uuid User uuid
     */
    @PutMapping("/{uuid}")
    public @ResponseBody Map replaceUser(@RequestBody Map<String, Object> payload,
                                         @PathVariable String uuid,
                                         HttpServletResponse response){
        User user = userRepository.findOneByUuid(uuid);
        if(user != null){
            user = ScimUtil.updateUser(payload, user);
            userRepository.save(user);
            response.setStatus(HttpStatus.OK.value());
            return ScimUtil.userToPayload(user);
        }else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }//replaceUser

    /**
     * Update user via patch op
     * @param payload Payload containing a SCIM PatchOp
     * @param uuid User uuid
     */
    @PatchMapping("/{uuid}")
    public @ResponseBody Map updateUser(@RequestBody Map<String, Object> payload,
                                        @PathVariable String uuid,
                                        HttpServletResponse response){
        //CONFIRM THAT THE PATCHOP IS VALID
        ScimUtil.validatePatchOp(payload);

        //FIND USER FOR UPDATE
        User user = userRepository.findOneByUuid(uuid);
        if(user != null) {
            //PARSE PATCH OP TO FIND ATTRIBUTE FOR UPDATE
            List<Map> operations = (List) payload.get(ScimUtil.PATCHOP_PLACEHOLDER);
            for (Map map : operations) {
                if (map.get("op") != null && map.get("op").equals("replace")) {
                    Map<String, Object> value = (Map) map.get("value");
                    if (value != null) {
                        for (Map.Entry key : value.entrySet()) {
                            //THIS METHOD SUPPORTS CHANGE ONLY IN THE ACTIVE STATUS
                            if (key.getKey().equals(ScimUtil.USER_ACTIVE)) {
                                user.setActive((Boolean) key.getValue());
                                userRepository.save(user);
                            }
                        }
                    }
                }
            }
            return ScimUtil.userToPayload(user);
        }else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }//updateUser

    /**
     * Delete user
     */
    @DeleteMapping("/{uuid}")
    public void deleteUser(@PathVariable String uuid, HttpServletResponse response) {
        User u = userRepository.findOneByUuid(uuid);
        if(u != null){
            //remove user from groups
            for(Group g: u.getGroups()){
                g.getUsers().remove(u);
            }
            //delete user
            userRepository.delete(u);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }//deleteUser

}

