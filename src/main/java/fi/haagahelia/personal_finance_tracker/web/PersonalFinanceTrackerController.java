package fi.haagahelia.personal_finance_tracker.web;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import fi.haagahelia.personal_finance_tracker.domain.CategoryRepository;
import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.domain.TransactionRepository;

import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersonalFinanceTrackerController {

    private TransactionRepository repository;
    private CategoryRepository cRepository;

    public PersonalFinanceTrackerController(TransactionRepository repository, CategoryRepository cRepository) {
        this.repository = repository;
        this.cRepository = cRepository;
    }

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
        return "redirect:/details";
    }
    
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editTransaction(@PathVariable("id") Long id, Model model) {
        model.addAttribute("transaction", repository.findById(id).get());
        model.addAttribute("categories", cRepository.findAll());
        return "add";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteTransaction(@PathVariable("id") Long id, Model model) {
        repository.deleteById(id);
        return "redirect:../details";
    }
}
