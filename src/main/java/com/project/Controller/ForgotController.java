package com.project.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.Dao.UserRepository;
import com.project.entity.User;
import com.project.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
		
	Random random = new Random();
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping("/forgot")
	public String openEmailFormHandler(HttpSession session) {
		  session.removeAttribute("message"); 
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("Email : "+ email);
		 
		// int otp = random.nextInt(999999);
		int otp = 100000 + random.nextInt(900000);
		System.out.println("Otp : "+otp);
		
		// Write code for send the otp in email 
		String subject = "OTP From SCM";
		String message = "<h1> OTP = "+otp+"</h1>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag) {
			session.setAttribute("otp",otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		}else {
			session.setAttribute("message","Check Your Email Id !!");
			
			return "forgot_email_form";
		}
		
	}
	
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {

	    Integer myOtp = (Integer) session.getAttribute("otp");

	    if(myOtp != null && myOtp == otp) {

	        session.removeAttribute("message");

	        String email = (String) session.getAttribute("email");
	        User user1 = this.userRepository.getUserByUserName(email);

	        if(user1 == null) {
	            session.setAttribute("message", "User Email Doesn't Exist !!");
	            return "forgot_email_form";
	        }

	        return "change_password";

	    } else {
	        session.setAttribute("message", "Invalid OTP !!");
	        return "verify_otp";
	    }
	}
	
	
			// Change Password handler 
	
	@PostMapping("/change-password")
	 public String changePassword(@RequestParam("newpassword") String newpassword , HttpSession session) {
		 String email = (String) session.getAttribute("email");
	     User user1 = this.userRepository.getUserByUserName(email);
	     user1.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
	     
		 this.userRepository.save(user1);
		 
		 return "redirect:/signin?change=password changed successfully...";
	 }

}
