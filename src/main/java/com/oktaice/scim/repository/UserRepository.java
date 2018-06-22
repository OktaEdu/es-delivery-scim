package com.oktaice.scim.repository;

import com.oktaice.scim.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>{

    //Find one by userName
    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    User findOneByUserName(@Param("userName") String username);

    //Find one by UUID
    @Query("SELECT u FROM User u WHERE u.uuid = :uuid")
    User findOneByUuid(@Param("uuid") String uuid);

    //Search by username
    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    Page<User> findByUsername(@Param("userName") String userName, Pageable page);

    //Search by status
    @Query("SELECT u FROM User u WHERE u.active = :active")
    Page<User> findByActive(@Param("active") Boolean active, Pageable page);

    //Search by last name
    @Query("SELECT u FROM User u WHERE u.lastName = :lastName")
    Page<User> findByLastName(@Param("lastName") String lastName, Pageable page);

    //Search by first name
    @Query("SELECT u FROM User u WHERE u.firstName = :firstName")
    Page<User> findByFirstName(@Param("firstName") String firstName, Pageable page);

}