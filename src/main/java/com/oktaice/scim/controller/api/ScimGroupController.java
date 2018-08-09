package com.oktaice.scim.controller.api;

import com.oktaice.scim.model.Group;
import com.oktaice.scim.model.ScimGroup;
import com.oktaice.scim.model.ScimGroupPatchOp;
import com.oktaice.scim.model.ScimListResponse;
import com.oktaice.scim.model.ScimPageFilter;
import com.oktaice.scim.repository.GroupRepository;
import com.oktaice.scim.repository.UserRepository;
import com.oktaice.scim.service.ScimService;
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
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody ScimGroup createGroup(@RequestBody ScimGroup scimGroup) {
        Group newGroup = scimService.scimGroupToGroup(scimGroup);
        groupRepository.save(newGroup);
        return scimService.groupToScimGroup(newGroup);
    }

    @GetMapping("/{uuid}")
    public ScimGroup getGroup(@PathVariable String uuid) {
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
                case ScimPageFilter.GROUP_NAME:
                    groups = groupRepository.findByName(searchValue, pageRequest);
                    break;
                case ScimPageFilter.GROUP_UUID:
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
    public @ResponseBody ScimGroup replaceGroup(@RequestBody ScimGroup scimGroup, @PathVariable String uuid) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        Group groupWithUpdates = scimService.scimGroupToGroup(scimGroup);
        copyGroup(groupWithUpdates, group);
        groupRepository.save(group);
        return scimService.groupToScimGroup(group);
    }

    private void copyGroup(Group from, Group to) {
        Assert.notNull(from, "From Group cannot be null");
        Assert.notNull(to, "To Group cannot be null");

        to.setDisplayName(from.getDisplayName());
        to.setUsers(from.getUsers());
    }

    @PatchMapping("/{uuid}")
    public @ResponseBody ScimGroup updateGroup(
        @RequestBody ScimGroupPatchOp scimGroupPatchOp, @PathVariable String uuid
    ) {
        //CONFIRM THAT THE PATCHOP IS VALID
        scimService.validateGroupPatchOp(scimGroupPatchOp);

        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        scimService.updateGroupByPatchOp(group, scimGroupPatchOp);
        groupRepository.save(group);
        return scimService.groupToScimGroup(group);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable String uuid) {
        Group group = groupRepository.findOneByUuid(uuid);
        if (group == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        groupRepository.delete(group);
    }
}
