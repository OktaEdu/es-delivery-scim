package com.oktaice.scim.controller.ui;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.User;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Controls the endpoint that display current users and groups
 */
@Controller
@RequestMapping("/")
public class AdminController {

    private GroupRepository groupRepository;
    private UserRepository userRepository;

    @Autowired
    public AdminController(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Loads home page
     */
    @RequestMapping(method = RequestMethod.GET)
    public String home(ModelMap model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        List<Group> groups = groupRepository.findAll();
        model.addAttribute("groups", groups);
        return "admin";
    }
}

