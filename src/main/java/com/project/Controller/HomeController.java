package com.project.Controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.Dao.UserRepository;
import com.project.entity.User;
import com.project.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String Home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String About(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String Signup(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	// Handler for the send the data 
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1 , @RequestParam(value = "agreement",defaultValue ="false") boolean agreement , Model model,RedirectAttributes redirectAttributes) {
		
		try {
			if(!agreement) {
				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			if(result1.hasErrors()) {
				System.out.println("ERROR : "+ result1);
				model.addAttribute("user", user);
				return "signup";
			}
			
		    user.setRole("ROLE_USER");
			//user.setRole("Role_User");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement :" + agreement);
			System.out.println("User :" + user);
			
			User result = this.userRepository.save(user);
			model.addAttribute("user",new User());
			
			//session.setAttribute("message", new Message("Successfull Registered !!","alert-success"));
			//return "signup"; 
			
			redirectAttributes.addFlashAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "redirect:/signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			//session.setAttribute("message", new Message("Something Went Wrong !!"+ e.getMessage(),"alert-danger"));
			//return "signup"; 
			
			 redirectAttributes.addFlashAttribute("message",
		                new Message("Something Went Wrong !! " + e.getMessage(), "alert-danger"));

		        return "redirect:/signup";
		}
		
		
	}

	@GetMapping("/signin")
	public String customLoginPage(Model model) {
		model.addAttribute("title", "Login Page - Smart Contact Manager");
		return "login";
	}
}
