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
@ConditionalOnProperty(name = "scim.service", havingValue = "wip")
@RequestMapping(USERS_LOCATION_BASE)
public class ScimUserController extends ScimBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ScimUserController.class);

    UserRepository userRepository;
    ScimService scimService;

    public ScimUserController(UserRepository userRepository, ScimService scimService) {
        this.userRepository = userRepository;
        this.scimService = scimService;
        logger.info("Using ScimUserController...");
    }

    /**
     * TODO: Implement the getUser method
     */
    @GetMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser getUser(@PathVariable String uuid) {
        //This is the line to delete
        return new ScimOktaIceUser();
        //Searches a Repository User by its uuid

        //Returns the Repository User and convert it to a SCIM User.

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

        /**
         * TODO: Complete the getUsers method
         */
        //Get a list of Repository Users from search and convert to a SCIM List Response


        //This is the line to delete
        return new ScimListResponse();
    }

    /**
     * TODO: Implement the createUser method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody ScimUser createUser(@RequestBody ScimUser scimUser) {
        //This is the line to delete
        return new ScimOktaIceUser();
        //Get new user's information

        //Save new user to DB

        //Returns the user information and convert it to a SCIM User

    }

    /**
     * TODO: Implement the replaceUser method
     */
    @PutMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser replaceUser(@RequestBody ScimUser scimUser, @PathVariable String uuid) {
        //This is the line to delete
        return new ScimOktaIceUser();

        //Finds the Repository User by uuid

        //Convert the SCIM User to a Repository User format if an existing Repository User can be found.

        //Copy attribute values from userWithUpdates to the existing Repository User

        //Save the updated value to DB

        //Return the updated user information and convert it to a SCIM User

    }

    /**
     * The copyUser method takes in two Repository Users.
     * It copy information from the first Repository User (from) to the second Repository User (to)
     */
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

    /**
     * TODO: Implement the updateUser method
     */
    @SuppressWarnings("unchecked")
    @PatchMapping("/{uuid}")
    public @ResponseBody ScimOktaIceUser updateUser(
        @RequestBody ScimUserPatchOp scimUserPatchOp, @PathVariable String uuid
    ) {
        //This is the line to delete
        return new ScimOktaIceUser();

        //Confirm that the ScimUserPatchOp is valid.

        //Finds the Repository User by uuid

        //If cannot find the user, returns "Resource not found" error message.


        // Do Patch Op (only active flag supported currently)


        //Return the updated user information and convert it to a SCIM User

    }

    /**
     * TODO: Review the deleteUser method
     */
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

