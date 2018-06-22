package com.oktaice.scim.repository;

import com.oktaice.scim.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    //Find one by group name
    @Query("SELECT g FROM Group g WHERE g.displayName = :displayName")
    Group findOneByName(@Param("displayName") String displayName);

    //Find one by UUID
    @Query("SELECT g FROM Group g WHERE g.uuid = :uuid")
    Group findOneByUuid(@Param("uuid") String uuid);

    //Search by group name
    @Query("SELECT g FROM Group g WHERE g.displayName = :displayName")
    Page<Group> findByName(@Param("displayName") String displayName, Pageable page);

    //Search by group id
    @Query("SELECT g FROM Group g WHERE g.uuid = :uuid")
    Page<Group> findByUuid(@Param("uuid") String uuid, Pageable page);

    //Search by group member (email)
    @Query("SELECT g FROM Group g JOIN g.users u WHERE u.email = :email")
    Page<Group> findByMemberEmail(@Param("email") String email, Pageable page);

    //Search by group member (uuid)
    @Query("SELECT g FROM Group g JOIN g.users u WHERE u.uuid = :uuid")
    Page<Group> findByMemberUuid(@Param("uuid") String uuid, Pageable page);
}