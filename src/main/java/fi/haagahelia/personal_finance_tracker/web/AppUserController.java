package fi.haagahelia.personal_finance_tracker.web;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fi.haagahelia.personal_finance_tracker.domain.AppUser;
import fi.haagahelia.personal_finance_tracker.domain.AppUserRepository;
import fi.haagahelia.personal_finance_tracker.domain.SignUpForm;
import jakarta.validation.Valid;

@Controller
public class AppUserController {

    private AppUserRepository repository;

    public AppUserController(AppUserRepository uReporitory) {
        this.repository = uReporitory;
    }
    
    @RequestMapping(value ="/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value ="/signup")
    public String addUser(Model model) {
        model.addAttribute("signupform", new SignUpForm());
        return "signup";
    }
    

    @RequestMapping(value = "saveuser", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("signupform") SignUpForm signupForm, BindingResult bindingResult) {
    	if (!bindingResult.hasErrors()) {
    		if (signupForm.getPassword().equals(signupForm.getPasswordCheck())) { 	
	    		String pwd = signupForm.getPassword();
		    	BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
		    	String hashPwd = bc.encode(pwd);
	
		    	AppUser newUser = new AppUser();
		    	newUser.setPasswordHash(hashPwd);
		    	newUser.setUsername(signupForm.getUsername());
				newUser.setEmail(signupForm.getEmail());
		    	newUser.setRole("USER");
		    	if (repository.findByUsername(signupForm.getUsername()) == null) { 
		    		repository.save(newUser);
		    	}
		    	else {
	    			bindingResult.rejectValue("username", "err.username", "Username already exists");    	
	    			return "signup";		    		
		    	}
    		}
    		else {
    			bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");    	
    			return "signup";
    		}
    	}
    	else {
    		return "signup";
    	}
    	return "redirect:/login";    	
    }
}
