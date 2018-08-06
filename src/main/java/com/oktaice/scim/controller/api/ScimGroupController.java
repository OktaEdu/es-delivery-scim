package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimGroup;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.ScimPageFilter;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
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

/**
 * SCIM API for group management
 */
@RestController
@RequestMapping("/scim/v2/Groups")
public class ScimGroupController extends ScimBaseController {
    GroupRepository groupRepository;
    UserRepository userRepository;
    ScimService scimService;

    public ScimGroupController(
        GroupRepository groupRepository, UserRepository userRepository, ScimService scimService
    ) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.scimService = scimService;
    }

    @PostMapping
    public @ResponseBody Map<String, Object> createGroup(
        @RequestBody Map<String, Object> params, HttpServletResponse response
    ) {
        Group newGroup = ScimUtil.toGroup(params, userRepository);
        groupRepository.save(newGroup);
        response.setStatus(201);
        return ScimUtil.groupToPayload(newGroup);
    }

    @GetMapping("/{uuid}")
    public ScimGroup getGroup(@PathVariable String uuid, HttpServletResponse response) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return scimService.groupToScimGroup(group);
    }

    @GetMapping
    public @ResponseBody ScimListResponse getGroups(@ModelAttribute ScimPageFilter scimPageFilter) {
        //GET STARTINDEX AND COUNT FOR PAGINATION
        PageRequest pageRequest = new PageRequest(scimPageFilter.getStartIndex() - 1, scimPageFilter.getCount());

        Page<Group> groups = null;

        //PARSE SEARCH FILTER
        Matcher match = scimPageFilter.parseFilter();
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
            //IF THERE'S NO FILTER, FIND ALL ENTRIES
            groups = groupRepository.findAll(pageRequest);
        }

        //GET LIST OF GROUPS FROM SEARCH AND CONVERT TO SCIM FOR RESPONSE
        List<Group> groupsFound = groups.getContent();
        return scimService.groupsToListResponse(
            groupsFound, scimPageFilter.getStartIndex(), scimPageFilter.getCount()
        );
    }

    @PutMapping("/{uuid}")
    public @ResponseBody Map<String, Object> replaceGroup(
        @RequestBody Map<String, Object> scimRequest, @PathVariable String uuid, HttpServletResponse response
    ) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        group = ScimUtil.updateGroup(scimRequest, group, userRepository);
        groupRepository.save(group);
        response.setStatus(HttpStatus.OK.value());
        return ScimUtil.groupToPayload(group);
    }

    @PatchMapping("/{uuid}")
    public @ResponseBody Map<String, Object> updateGroup(
        @RequestBody Map<String, Object> scimPatchOp, @PathVariable String uuid, HttpServletResponse response
    ) {
        //CONFIRM THAT THE PATCHOP IS VALID
        ScimUtil.validatePatchOp(scimPatchOp);

        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        group = ScimUtil.updateGroupPatchOp(scimPatchOp, group, userRepository);
        groupRepository.save(group);
        response.setStatus(HttpStatus.OK.value());
        return ScimUtil.groupToPayload(group);
    }

    @DeleteMapping("/{uuid}")
    public void deleteGroup(@PathVariable String uuid, HttpServletResponse response) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        groupRepository.delete(group);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
