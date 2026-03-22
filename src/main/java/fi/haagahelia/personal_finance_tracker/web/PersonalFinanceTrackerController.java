package fi.haagahelia.personal_finance_tracker.web;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import fi.haagahelia.personal_finance_tracker.domain.CategoryRepository;
import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.domain.TransactionRepository;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersonalFinanceTrackerController {

    @Autowired
    private TransactionRepository repository;
    private CategoryRepository cRepository;

    public PersonalFinanceTrackerController(TransactionRepository repository, CategoryRepository cRepository) {
        this.repository = repository;
        this.cRepository = cRepository;
    }
    
    // @RequestMapping(value = "/details")
    // public String transactionList(Model model) {
    //     UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //     System.out.println("USERNAME:" + user.getUsername());
    //     model.addAttribute("name", user.getUsername());
    //     model.addAttribute("transactions", repository.findAll());
    //     return "transactionlist";
    // }

    @RequestMapping(value = "/details", method = {RequestMethod.GET})
    public String transactionList(Model model){
        model.addAttribute("transactions", repository.findAll());
        return "details";
    }

    @RequestMapping(value = "/add")
    public String addTransaction (Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("categories", cRepository.findAll());
        return "add";
    }

    @RequestMapping(value = "/save", method=RequestMethod.POST)
    public String save(Transaction transaction) {
        repository.save(transaction);
        return "redirect:details";
    }
    
}
