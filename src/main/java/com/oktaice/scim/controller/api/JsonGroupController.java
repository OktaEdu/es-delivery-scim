package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.utils.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * REST API for group management
 */
@RestController
@RequestMapping("/api/v1/groups")
public class JsonGroupController {
    GroupRepository groupRepository;
    UserRepository userRepository;

    public JsonGroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository= groupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Return all groups
     */
    @GetMapping
    public  @ResponseBody Map getGroups(){
        return JsonUtil.groupsToPayload(groupRepository.findAll());
    }//getGroups

    /**
     * Create group while validating the membership ids
     * id's that don't match will be ignored
     */
    @PostMapping
    public @ResponseBody Map createGroup(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        Group newGroup = JsonUtil.toGroup(params, userRepository);
        groupRepository.save(newGroup);
        response.setStatus(201);
        return JsonUtil.groupToPayload(newGroup);
    }//createGroup

    /**
     * Get specific group
     * @param groupId Group Id
     */
    @GetMapping("/{groupId}")
    public Map getGroup(@PathVariable int groupId, HttpServletResponse response) {
        Group g = groupRepository.findOne(groupId);
        response.setStatus((g != null) ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value());//200 or 404
        return JsonUtil.groupToPayload(g);
    }//getGroup

    /**
     * Replace group via put
     * @param groupId Group Id
     */
    @PutMapping("/{groupId}")
    public @ResponseBody Map replaceGroup(@RequestBody Map<String, Object> payload,
                                          @PathVariable int groupId,
                                          HttpServletResponse response) {
        Group group = groupRepository.findOne(groupId);
        if(group != null){
            group = JsonUtil.updateGroup(payload, group, userRepository);
            groupRepository.save(group);
            response.setStatus(HttpStatus.OK.value());
            return JsonUtil.groupToPayload(group);
        }else{
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
    }//replaceGroup

    /**
     * Update group via patch
     * @param groupId Group Id
     */
    @PatchMapping("/{groupId}")
    public @ResponseBody Map updateGroup(@RequestBody Map<String, Object> payload,
                                         @PathVariable int groupId,
                                         HttpServletResponse response) {
        return this.replaceGroup(payload, groupId, response);
    }//updateGroup

    /**
     * Delete group
     * @param groupId Group Id
     */
    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable int groupId, HttpServletResponse response) {
        groupRepository.delete(groupId);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }//deleteGroup

}
