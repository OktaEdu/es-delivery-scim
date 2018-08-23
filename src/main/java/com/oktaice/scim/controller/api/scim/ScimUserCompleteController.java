package com.oktaice.scim.controller.api.scim;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.scim.ScimListResponse;
import com.oktaice.scim.model.scim.ScimOktaIceUser;
import com.oktaice.scim.model.scim.support.ScimPageFilter;
import com.oktaice.scim.model.scim.ScimUser;
import com.oktaice.scim.model.scim.ScimUserPatchOp;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.regex.Matcher;

import static com.oktaice.scim.service.ScimService.USERS_LOCATION_BASE;

@RestController
@ConditionalOnProperty(name = "scim.service", havingValue = "complete")
@RequestMapping(USERS_LOCATION_BASE)
public class ScimUserCompleteController extends ScimBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ScimUserCompleteController.class);

    UserRepository userRepository;
    ScimService scimService;

    public ScimUserCompleteController(UserRepository userRepository, ScimService scimService) {
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimUserCompleteController...");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody ScimUser createUser(@RequestBody ScimUser scimUser) {
        User newUser = scimService.scimUserToUser(scimUser);
        userRepository.save(newUser);
        return scimService.userToScimOktaIceUser(newUser);
    }

    @GetMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser getUser(@PathVariable String uuid) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return scimService.userToScimOktaIceUser(user);
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
                case ScimPageFilter.USER_USERNAME:
                    users = userRepository.findByUsername(searchValue, pageRequest);
                    break;
                case ScimPageFilter.USER_ACTIVE:
                    users = userRepository.findByActive(Boolean.valueOf(searchValue), pageRequest);
                    break;
                case ScimPageFilter.USER_FIRST_NAME:
                    users = userRepository.findByFirstName(searchValue, pageRequest);
                    break;
                case ScimPageFilter.USER_LAST_NAME:
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
        return scimService.usersToListResponse(
            foundUsers, scimPageFilter.getStartIndex(), scimPageFilter.getCount()
        );
    }

    @PutMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser replaceUser(@RequestBody ScimUser scimUser, @PathVariable String uuid) {
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        User userWithUpdates = scimService.scimUserToUser(scimUser);
        copyUser(userWithUpdates, user);
        userRepository.save(user);
        return scimService.userToScimOktaIceUser(user);
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
    public @ResponseBody ScimOktaIceUser updateUser(
        @RequestBody ScimUserPatchOp scimUserPatchOp, @PathVariable String uuid
    ) {
        //CONFIRM THAT THE PATCHOP IS VALID
        scimService.validateUserPatchOp(scimUserPatchOp);

        //FIND USER FOR UPDATE
        User user = userRepository.findOneByUuid(uuid);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        // Do Patch Op (only active flag supported currently)
        boolean activeReplace = scimUserPatchOp.getOperations().get(0).getValue().getActive();
        if (activeReplace != user.getActive()) {
            user.setActive(activeReplace);
            userRepository.save(user);
        }

        return scimService.userToScimOktaIceUser(user);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String uuid) {
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
    }
}

