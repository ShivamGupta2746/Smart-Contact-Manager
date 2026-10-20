package com.project.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.Dao.ContactsRepository;
import com.project.Dao.UserRepository;
import com.project.entity.Contact;
import com.project.entity.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactsRepository contactsRepository;
	
	// Search Handler 
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query,Principal principal){
		 
		System.out.println("Query :"+ query);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts = this.contactsRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}

}
