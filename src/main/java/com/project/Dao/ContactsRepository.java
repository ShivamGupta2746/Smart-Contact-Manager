package com.project.Dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.entity.Contact;
import com.project.entity.User;

import java.util.List;

public interface ContactsRepository extends JpaRepository<Contact, Integer>{

	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pePageable);

	// Search method query make by ourself 
	public List<Contact> findByNameContainingAndUser(String name , User user);
	
	
}
