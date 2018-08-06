package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.ScimOktaIceUser;
import com.oktaice.scim.model.ScimPageFilter;
import com.oktaice.scim.model.ScimUser;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimConverterService;
import com.oktaice.scim.utils.ScimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
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

import static com.oktaice.scim.service.ScimConverterService.USERS_LOCATION_BASE;

@RestController
@RequestMapping(USERS_LOCATION_BASE)
public class ScimUserController extends ScimBaseController {

    UserRepository userRepository;
    ScimConverterService scimConverterService;

    public ScimUserController(UserRepository userRepository, ScimConverterService scimConverterService) {
        this.userRepository = userRepository;
        this.scimConverterService = scimConverterService;
    }

    @PostMapping
    public @ResponseBody ScimUser createUser(
        @RequestBody Map<String, Object> scimRequest, HttpServletResponse response
    ) {
        ScimUser scimUser = scimConverterService.mapToScimUser(scimRequest);
        User newUser = scimConverterService.scimUserToUser(scimUser);
        userRepository.save(newUser);
        response.setStatus(HttpStatus.CREATED.value());
        return scimConverterService.userToScimOktaIceUser(newUser);
    }

    @GetMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser getUser(@PathVariable String uuid, HttpServletResponse response) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return scimConverterService.userToScimOktaIceUser(user);
    }

    @GetMapping
    public @ResponseBody ScimListResponse getUsers(@ModelAttribute ScimPageFilter scimPageFilter) {
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
        return scimConverterService.usersToListResponse(
            foundUsers, scimPageFilter.getStartIndex(), scimPageFilter.getCount()
        );
    }

    @PutMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser replaceUser(
        @RequestBody Map<String, Object> scimRequest, @PathVariable String uuid, HttpServletResponse response
    ) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        ScimUser scimUser = scimConverterService.mapToScimUser(scimRequest);
        User userWithUpdates = scimConverterService.scimUserToUser(scimUser);
        copyUser(userWithUpdates, user);
        userRepository.save(user);
        response.setStatus(HttpStatus.OK.value());
        return scimConverterService.userToScimOktaIceUser(user);
    }

    private void copyUser(User from, User to) {
        Assert.notNull(from, "From User cannot be null");
        Assert.notNull(to, "To User cannot be null");

        to.setActive(from.getActive());
        to.setUserName(from.getUserName());

        to.setEmail(from.getEmail());

        to.setLastName(from.getLastName());
        to.setMiddleName(from.getMiddleName());
        to.setFirstName(from.getFirstName());

        to.setCostCenter(from.getCostCenter());
        to.setEmployeeNumber(from.getEmployeeNumber());

        to.setFavoriteIceCream(from.getFavoriteIceCream());
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

