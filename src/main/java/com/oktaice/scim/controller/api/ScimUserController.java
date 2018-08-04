package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimPageFilter;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.utils.ScimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/scim/v2/Users")
public class ScimUserController extends ScimBaseController {

    UserRepository userRepository;

    public ScimUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public @ResponseBody Map<String, Object> createUser(
        @RequestBody Map<String, Object> scimRequest, HttpServletResponse response
    ) {
        User newUser = ScimUtil.toUser(scimRequest);
        userRepository.save(newUser);
        response.setStatus(HttpStatus.CREATED.value());
        return ScimUtil.userToPayload(newUser);
    }

    @GetMapping("/{uuid}")
    public @ResponseBody Map<String, Object> getUser(@PathVariable String uuid, HttpServletResponse response) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return ScimUtil.userToPayload(user);
    }

    @GetMapping
    public @ResponseBody Map<String, Object> getUsers(@ModelAttribute ScimPageFilter scimPageFilter) {

        //GET STARTINDEX AND COUNT FOR PAGINATION
        PageRequest pageRequest =
            new PageRequest(scimPageFilter.getStartIndex() - 1, scimPageFilter.getCount());

        Page<User> users = null;

        //PARSE SEARCH FILTER
        Matcher match = scimPageFilter.parseFilter();
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
            //IF THERE'S NO FILTER, FIND ALL ENTRIES
            users = userRepository.findAll(pageRequest);
        }
        //GET LIST OF USERS FROM SEARCH AND CONVERT TO SCIM FOR RESPONSE
        List<User> foundUsers = users.getContent();
        return ScimUtil.usersToPayload(foundUsers, scimPageFilter.getStartIndex(), scimPageFilter.getCount());
    }

    @PutMapping("/{uuid}")
    public @ResponseBody Map<String, Object> replaceUser(
        @RequestBody Map<String, Object> scimRequest, @PathVariable String uuid, HttpServletResponse response
    ) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        user = ScimUtil.updateUser(scimRequest, user);
        userRepository.save(user);
        response.setStatus(HttpStatus.OK.value());
        return ScimUtil.userToPayload(user);
    }

    @SuppressWarnings("unchecked")
    @PatchMapping("/{uuid}")
    public @ResponseBody Map<String, Object> updateUser(
        @RequestBody Map<String, Object> scimPatchOp, @PathVariable String uuid, HttpServletResponse response
    ) {
        //CONFIRM THAT THE PATCHOP IS VALID
        ScimUtil.validatePatchOp(scimPatchOp);

        //FIND USER FOR UPDATE
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        //PARSE PATCH OP TO FIND ATTRIBUTE FOR UPDATE
        List<Map<String, Object>> operations =
            (List<Map<String, Object>>) scimPatchOp.get(ScimUtil.PATCHOP_PLACEHOLDER);
        for (Map<String, Object> map : operations) {
            if ("replace".equals(map.get("op"))) {
                Map<String, Object> operation = (Map<String, Object>) map.get("value");
                if (operation != null && operation.keySet().contains(ScimUtil.USER_ACTIVE)) {
                    for (String key : operation.keySet()) {
                        //THIS METHOD SUPPORTS CHANGE ONLY IN THE ACTIVE STATUS
                        if (key.equals(ScimUtil.USER_ACTIVE)) {
                            user.setActive((Boolean) operation.get(key));
                            userRepository.save(user);
                        }
                    }
                }
            }
        }
        return ScimUtil.userToPayload(user);
    }

    @DeleteMapping("/{uuid}")
    public void deleteUser(@PathVariable String uuid, HttpServletResponse response) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        //remove user from groups
        for (Group g : user.getGroups()) {
            g.getUsers().remove(user);
        }
        
        //delete user
        userRepository.delete(user);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}

