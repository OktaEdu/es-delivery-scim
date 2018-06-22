package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.repository.GroupRepository;
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

/**
 * SCIM API for group management
 */
@RestController
@RequestMapping("/scim/v2/Groups")
public class ScimGroupController extends ScimBaseController {
    GroupRepository groupRepository;
    UserRepository userRepository;

    public ScimGroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Return all groups with pagination
     * @return ListResponse containing several Groups
     */
    @GetMapping
    public  @ResponseBody Map getGroups(@RequestParam Map<String, String> params){
        Page<Group> groups = null;

        //GET STARTINDEX AND COUNT FOR PAGINATION
        int pageCount = (params.get(ScimUtil.LIST_RESPONSE_COUNT) != null) ? Integer.parseInt(params.get(ScimUtil.LIST_RESPONSE_COUNT)) : 100;
        int startIndex = (params.get(ScimUtil.LIST_RESPONSE_INDEX) != null) ? Integer.parseInt(params.get(ScimUtil.LIST_RESPONSE_INDEX)) : 1;
        startIndex = (startIndex < 1) ? 1 : startIndex;
        PageRequest pageRequest = new PageRequest((startIndex-1), pageCount);

        //PARSE SEARCH FILTER
        String filter = params.get(ScimUtil.LIST_RESPONSE_SEARCH_FILTER);
        if (filter != null && filter.contains("eq")) {
            Matcher match = ScimUtil.parseFilter(filter);
            if (match.find()) {
                String searchKeyName = match.group(1);
                String searchValue = match.group(2);
                //IF THERE'S A VALID FILTER, USE THE PROPER METHOD FOR GROUP SEARCH
                switch (searchKeyName) {
                    case ScimUtil.GROUP_NAME:
                        groups = groupRepository.findByName(searchValue, pageRequest);
                        break;
                    case ScimUtil.GROUP_UUID:
                        groups = groupRepository.findByUuid(searchValue, pageRequest);
                        break;
                    default:
                        throw new HttpClientErrorException(HttpStatus.NOT_IMPLEMENTED, "Filter not implemented");
                }
            } else {
                //IF FILTER IS NOT RECOGNIZED, FIND ALL ENTRIES
                groups = groupRepository.findAll(pageRequest);
            }
        } else {
            //IF THERE'S NO FILTER, FIND ALL ENTRIES
            groups = groupRepository.findAll(pageRequest);
        }
        //GET LIST OF GROUPS FROM SEARCH AND CONVERT TO SCIM FOR RESPONSE
        List<Group> groupsFound = groups.getContent();
        return ScimUtil.groupsToPayload(groupsFound, Optional.of(startIndex), Optional.of(pageCount));
    }//getGroups


    /**
     * Create group while validating the membership ids
     * id's that don't match will be ignored
     */
    @PostMapping
    public @ResponseBody Map createGroup(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        Group newGroup = ScimUtil.toGroup(params, userRepository);
        groupRepository.save(newGroup);
        response.setStatus(201);
        return ScimUtil.groupToPayload(newGroup);
    }//createGroup

    /**
     * Get specific group
     * @param uuid Group UUID
     */
    @GetMapping("/{uuid}")
    public Map getGroup(@PathVariable String uuid, HttpServletResponse response) {
        Group g = groupRepository.findOneByUuid(uuid);
        if(g == null){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return ScimUtil.groupToPayload(g);
    }//toGroup

    /**
     * Replace group via put
     * @param uuid Group UUID
     */
    @PutMapping("/{uuid}")
    public @ResponseBody Map replaceGroup(@RequestBody Map<String, Object> payload,
                                          @PathVariable String uuid,
                                          HttpServletResponse response) {
        Group group = groupRepository.findOneByUuid(uuid);
        if(group != null){
            group = ScimUtil.updateGroup(payload, group, userRepository);
            groupRepository.save(group);
            response.setStatus(HttpStatus.OK.value());
            return ScimUtil.groupToPayload(group);
        }else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }//replaceGroup

    /**
     * Update group via post
     * @param uuid Group UUID
     */
    @PatchMapping("/{uuid}")
    public @ResponseBody Map updateGroup(@RequestBody Map<String, Object> payload,
                                         @PathVariable String uuid,
                                         HttpServletResponse response) {
        throw new HttpClientErrorException(HttpStatus.NOT_IMPLEMENTED, "PatchOp not implemented");
    }//replaceGroup

    /**
     * Delete group
     * @param uuid Group UUID
     */
    @DeleteMapping("/{uuid}")
    public void deleteGroup(@PathVariable String uuid, HttpServletResponse response) {
        Group g = groupRepository.findOneByUuid(uuid);
        if(g != null) {
            groupRepository.delete(g);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }//deleteGroup

}
