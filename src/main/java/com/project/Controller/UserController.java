package com.project.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.aspectj.apache.bcel.util.ClassPath;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.Dao.ContactsRepository;
import com.project.Dao.MyOrderRepository;
import com.project.Dao.UserRepository;
import com.project.entity.Contact;
import com.project.entity.MyOrder;
import com.project.entity.User;
import com.project.helper.Message;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayClient.*;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactsRepository contactsRepository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	
				// Add Common Data for all handler
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("UserName :" + userName);
		
		// Find the the user using email 
		
		User user = userRepository.getUserByUserName(userName);
		System.out.println("User :" + user);
		
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("UserName :" + userName);
		
		// Find the the user using email 
		
		User user = userRepository.getUserByUserName(userName);
		System.out.println("User :" + user);
		
		model.addAttribute("user", user);
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
			// Add Contact Form Handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
			// Processing form handler 
	@PostMapping("/process-contact")
	public String processForm(@ModelAttribute Contact contact, HttpSession session, @RequestParam("profileImage") MultipartFile file, Principal principal) {
		
		try {
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		 if (!file.isEmpty()) {
		        contact.setImage(file.getOriginalFilename()); 
		        File saveFile = new ClassPathResource("static/image").getFile();
		       
		        Path path =  Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		        
		        Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
		        
		        System.out.println("Image uploaded Successfully");
		    }
		 else {
			
			 System.out.println("Imagge is not Uploaded");
			 contact.setImage("default.jpg");
		}
		
		
		
		user.getContacts().add(contact);
		contact.setUser(user);
		
		this.userRepository.save(user);
		
		System.out.println("Data : "+contact );
		
		System.out.println("Added Contacts");
		//System.out.println("File Name: " + file.getOriginalFilename());
	    //System.out.println("File Size: " + file.getSize());

				// Message success
		session.setAttribute("message", new Message("Your Contact Is Added !! Add More..","success"));
	   
		}catch(Exception e) {
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
			// Message Error
			session.setAttribute("message", new Message("Something Went Wrong !! Try Again","danger"));
		   
		}
		return"normal/add_contact_form";
	}
	
	    
			// This is the show conatct controller
			//Pegination for only 5 contacts per page 
	@GetMapping("/show-contacts/{page}")
	public String showViewHandler(@PathVariable("page") Integer page, Model model,Principal principal) {
		model.addAttribute("title", "show contacts page");
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
				// Pagination page start
				// It takes the two information current page and per page information
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = contactsRepository.findContactByUser(user.getId(),pageable); 
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return"normal/show_contacts";
	}
	
			// Showing particular contacts details
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId , Model model, Principal principal){
		System.out.println("CID : "+ cId);
		
		Optional<Contact> optionalContact =  this.contactsRepository.findById(cId);
		Contact contact = optionalContact.get();
		
		//  Dusra banda dusre ka contact na dekh sake 
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId() == contact.getUser().getId()) {
		model.addAttribute("contact", contact);
		model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}
	
	
			// Delete the contact 
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId , Model model, HttpSession session, Principal principal) {
		
		System.out.println("CID" + cId);
		
		Contact contact = this.contactsRepository.findById(cId).get();
		
		// Check Assignment ..
		
		System.out.println("Contact" + contact.getcId());
		
		
		//     contact.setUser(null);
	   //      this.contactsRepository.delete(contact);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);
		
		System.out.println("Deleted");
		
		session.setAttribute("message", new Message("Deleted Successfully..." , "success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	
				// update form the contact details 
	 @PostMapping("/update-contact/{cid}")
	 public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactsRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		 return"normal/update_form";
	 }
	 
	 		//Update Process Handler 
	 @PostMapping("/process-update")
	 public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file , Model m, HttpSession session, Principal principal) {
		 
		 try {
			 		//Old contact details fetch
			 Contact oldContactDetails = this.contactsRepository.findById(contact.getcId()).get();
			 
			 if(!file.isEmpty()) {
				 // File work code
				 
				 	// Delete Old pic 
				 File deleteFile = new ClassPathResource("static/image").getFile();
				 File file1 = new File(deleteFile , oldContactDetails.getImage());
				 file1.delete();
				 
				 	// Update new Pic
				 File saveFile = new ClassPathResource("static/image").getFile();
			       
			     Path path =  Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			        
			     Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
			     contact.setImage(file.getOriginalFilename());   		 
				 
			 }else {
				 contact.setImage(oldContactDetails.getImage());
			 }
			
			 User user = this.userRepository.getUserByUserName(principal.getName());
			 contact.setUser(user);
			 this.contactsRepository.save(contact);
			 
			 session.setAttribute("message", new Message("Your Contact Is Updated..","success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 System.out.println("Contact Name :" + contact.getName());
		 System.out.println("Contact Id :" + contact.getcId());
		 return "redirect:/user/"+contact.getcId()+"/contact";
	 }
	 
	 
	 		// This is the profile handler 
	 @GetMapping("/profile")
	 public String yourProfile(Model model) {
		 model.addAttribute("title", "Profile Page");
		 return "normal/profile";
	 }
	 
	 	// This is the settings handler 
	 @GetMapping("/settings")
	 public String openSetting(Model model) {
		 model.addAttribute("title", "Settings");
		 return "normal/settings";
	 }
	 
	 
	 		// This is the change password handler
	 @PostMapping("/change-password")
	 public String changePassword(@RequestParam("oldPassword") String oldPassword , @RequestParam("newPassword") String newPassword,Principal principal, HttpSession session) {
		 System.out.println("Old Password :"+ oldPassword);
		 System.out.println("New Password :"+ newPassword);
		 
		 String userName = principal.getName();
		 User currentUser = this.userRepository.getUserByUserName(userName);
		 System.out.println("Password : "+currentUser.getPassword());
		 
		 if(this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword())) {
			 // Change Password 
			 currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			 this.userRepository.save(currentUser);
			 
		session.setAttribute("message", new Message("Password Changed Successfully..." , "success"));

			 
		 }else {
			 // Error Not change the password 
		session.setAttribute("message", new Message("Enter Correct Old Password.." , "danger"));

		 }
		 
		 return "redirect:/user/index";
	 }
	 
	 // Payment integration code in java 
	@PostMapping("/create_order")
	 @ResponseBody
	 public String createOrder(@RequestBody Map<String, Object> data, Principal principal)throws RazorpayException {
		//System.out.println("This is theh order function created ");
		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient client = new RazorpayClient("rzp_test_TlNsrzm0TF2tBw", "oE6KNDLE7vGWaTGKYQ4jJOsx");
		
		JSONObject ob = new JSONObject();
		ob.put("amount", amt*100);
		ob.put("currency", "INR");
		ob.put("receipt", "shivi_123");

		Order order = client.orders.create(ob);
		System.out.println(order);
		
		// Save order in the database
		
		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepository.save(myOrder);
		
		 return order.toString();
	 }
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		
		MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myOrder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","Updated..."));
	}
}
