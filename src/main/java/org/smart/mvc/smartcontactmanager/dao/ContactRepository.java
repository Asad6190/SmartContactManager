package org.smart.mvc.smartcontactmanager.dao;

import org.smart.mvc.smartcontactmanager.entities.Contact;
import org.smart.mvc.smartcontactmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ContactRepository extends JpaRepository<Contact, Integer> {

    //Pagination kay liye

    @Query("from Contact as c where c.user.id =:userId")
    //Current Page
    //Contact Per Page

    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);



    public List<Contact> findByNameContainingAndUser(String name, User user);

}
