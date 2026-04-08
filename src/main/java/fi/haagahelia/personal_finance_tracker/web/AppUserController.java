package fi.haagahelia.personal_finance_tracker.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.haagahelia.personal_finance_tracker.domain.AppUser;
import fi.haagahelia.personal_finance_tracker.domain.AppUserRepository;
import fi.haagahelia.personal_finance_tracker.domain.SignUpForm;
import fi.haagahelia.personal_finance_tracker.domain.ResetPasswordForm;
import jakarta.servlet.http.HttpServletRequest;
import fi.haagahelia.personal_finance_tracker.domain.UserNotFoundException;
import jakarta.validation.Valid;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.UUID;

@Controller
public class AppUserController {

    private AppUserRepository uRepository;

    public AppUserController(AppUserRepository uReporitory) {
        this.uRepository = uReporitory;
    }
    
	@Autowired
	private JavaMailSender mailSender; 

	// Login
    @RequestMapping(value ="/login")
    public String login() {
        return "login";
    }

	// Show sign up form
    @RequestMapping(value ="/signup")
    public String addUser(Model model) {
        model.addAttribute("signupform", new SignUpForm());
        return "signup";
    }
    

    @RequestMapping(value = "saveuser", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("signupform") SignUpForm signupForm, BindingResult bindingResult, HttpServletRequest request) throws MessagingException {
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
		    	if (uRepository.findByUsername(signupForm.getUsername()) == null) { 
		    		uRepository.save(newUser);
		    		
		    		// Send verification email
		    		UUID uuid = UUID.randomUUID();
		    		String token = uuid.toString().replaceAll("-", "");
		    		newUser.setVerificationToken(token);
		    		uRepository.save(newUser);
		    		
		    		String url = request.getRequestURL().toString();
		    		String verificationLink = url.replace(request.getServletPath(), "") + "/verify_email?token=" + token;
		    		sendVerificationEmail(signupForm.getEmail(), verificationLink);
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

	// Verify user email
	@RequestMapping(value = "/verify_email", method = RequestMethod.GET)
	public String verifyEmail(@RequestParam(value = "token") String token, Model model) {
	    AppUser user = uRepository.findByVerificationToken(token);
	    
	    if (user != null) {
	        user.setEnabled(true);
	        user.setVerificationToken(null);
	        uRepository.save(user);
	        
	        return "verify_email";
	    } else {
	        return "token_error";
	    }
	    
	}

	// Direct user to the forgot password page
	@RequestMapping(value = "/forgot_password", method = RequestMethod.GET)
	public String forgotPassword(Model model) {
		return "forgotpassword";
	}

	// Send a reset email to the user
	@RequestMapping(value = "/forgot_password", method = RequestMethod.POST)
	public String processForgotPasword(HttpServletRequest request, Model model) throws MessagingException {
		try {
			String email = request.getParameter("email");
			UUID uuid = UUID.randomUUID();
			String token = uuid.toString().replaceAll("-", "");

			AppUser appUser = uRepository.findByEmail(email);
			
			if (appUser == null) {
				throw new UserNotFoundException("Could not find the user with this email.");
			} else if (!appUser.isEnabled()) {
				throw new UserNotFoundException("The user is not verified. Please check your email for verification link");
			} else {
				appUser.setResetToken(token);
				uRepository.save(appUser);
			}

			String url = request.getRequestURL().toString();

			// gets rid of /forgot_password
			String passwordResetLink = url.replace(request.getServletPath(), "") + "/reset_password?token=" + token;
			System.out.println(passwordResetLink);

			sendResetEmail(email, passwordResetLink);

			model.addAttribute("message", "We have sent you a reset link. Please check your email.");

		} catch (UserNotFoundException exeption) {
			model.addAttribute("error", exeption.getMessage());
		} catch (MessagingException exception) {
			model.addAttribute("error", "Error while sending email");
		}
		return "forgotpassword";
	}

	// Direct user to the reset password form if the token is valid
	@RequestMapping(value = "/reset_password", method = RequestMethod.GET)
	public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
		AppUser user = uRepository.findByResetToken(token);
		model.addAttribute("token", token);
		model.addAttribute("resetform", new ResetPasswordForm());

		if (user == null) {
			return "token_error";
		}

		return "reset_password";
	}

	// Reset user password
	@RequestMapping(value = "/reset_password", method = RequestMethod.POST)
	public String showResetPasswordForm(@RequestParam(value = "token") String token,
			@Valid @ModelAttribute("resetform") ResetPasswordForm resetForm, BindingResult bindingResult) {
		AppUser appUser = uRepository.findByResetToken(token);

		if (!bindingResult.hasErrors()) {
			if (resetForm.getPassword().equals(resetForm.getPasswordCheck())) {
				String pwd = resetForm.getPassword();
				BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
				String hashPwd = bc.encode(pwd);

				appUser.setPasswordHash(hashPwd);
				appUser.setResetToken(null);

				uRepository.save(appUser);
			}
		} else {
			bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");
			return "reset_password";
		}

		return "redirect:/login";
	}
	
	// Sending verification email
	private void sendVerificationEmail(String email, String verificationLink) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("languageapp4@gmail.com");
		helper.setTo(email);

		String content = "<p>Hello,</p>"
				+ "<p>Thank you for registering. Please verify your email by clicking the link below:</p>"
				+ "<p><a href=\"" + verificationLink + "\">Verify my email</a></p>";

		helper.setSubject("Email Verification");
		helper.setText(content, true);

		mailSender.send(message);

	}
	
	// Send password reset email
	private void sendResetEmail(String email, String passwordResetLink) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("languageapp4@gmail.com");
		helper.setTo(email);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password</p>"
				+ "<p>Click the link below to reset your password</p>" + "<p><a href=\"" + passwordResetLink
				+ "\">Change my password</a></p>";

		helper.setSubject("Password reset link");
		helper.setText(content, true);

		mailSender.send(message);
	}

}

