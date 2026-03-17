package fi.haagahelia.personal_finance_tracker.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppUserController {

    @RequestMapping(value ="/login")
    public String login() {
        return "login";
    }
}
