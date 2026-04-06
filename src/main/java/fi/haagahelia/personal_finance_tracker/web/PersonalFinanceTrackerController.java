package fi.haagahelia.personal_finance_tracker.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.security.Principal;
import java.time.YearMonth;

// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import fi.haagahelia.personal_finance_tracker.domain.AppUser;
import fi.haagahelia.personal_finance_tracker.domain.AppUserRepository;
import fi.haagahelia.personal_finance_tracker.domain.Budget;
import fi.haagahelia.personal_finance_tracker.domain.BudgetRepository;
import fi.haagahelia.personal_finance_tracker.domain.CategoryRepository;
import fi.haagahelia.personal_finance_tracker.domain.FinancialResultDTO;
import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.domain.TransactionRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersonalFinanceTrackerController {

    private TransactionRepository repository;
    private CategoryRepository cRepository;
    private BudgetRepository bRepository;
    private AppUserRepository uRepository;

    public PersonalFinanceTrackerController(TransactionRepository repository, CategoryRepository cRepository,
            BudgetRepository bRepository, AppUserRepository uRepository) {
        this.repository = repository;
        this.cRepository = cRepository;
        this.bRepository = bRepository;
        this.uRepository = uRepository;
    }

    @RequestMapping(value = "/details", method = { RequestMethod.GET })
    public String transactionList(Model model, Principal principal) {
        String username = principal.getName();
        AppUser currentUser = uRepository.findByUsername(username);
        List<Transaction> details;

        if (currentUser.getRole().equals("ADMIN")) {
            details = repository.findAll();
        } else {
            details = repository.findByUser(currentUser);
        }
        model.addAttribute("transactions", details);
        return "details";
    }

    @RequestMapping(value = "/add")
    public String addTransaction(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("categories", cRepository.findAll());
        return "add";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "add";
        }

        String username = principal.getName();
        AppUser currentUser = uRepository.findByUsername(username);
        transaction.setUser(currentUser);
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

    @RequestMapping(value = "/addbudget")
    public String addBudget(Model model, Principal principal) {
        String username = principal.getName();
        AppUser currentUser = uRepository.findByUsername(username);
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", cRepository.findAll());
        if (currentUser.getRole().equals("ADMIN")) {
            model.addAttribute("budgets", bRepository.findAll());
        } else {
            model.addAttribute("budgets", bRepository.findByUser(currentUser));
        }
        return "addbudget";
    }

    @RequestMapping(value = "/savebudget", method = RequestMethod.POST)
    public String saveBudget(Budget budget, Principal principal) {
        String username = principal.getName();
        AppUser currentUser = uRepository.findByUsername(username);
        budget.setUser(currentUser);
        bRepository.save(budget);
        return "redirect:/addbudget";
    }

    @RequestMapping(value = "/editbudget/{id}", method = RequestMethod.GET)
    public String editBudget(@PathVariable("id") Long id, Model model, Principal principal) {
        model.addAttribute("budget", bRepository.findById(id).get());
        model.addAttribute("categories", cRepository.findAll());
        model.addAttribute("budgets", bRepository.findAll());
        return "addbudget";
    }

    @RequestMapping(value = "/deletebudget/{id}", method = RequestMethod.GET)
    public String deleteBudget(@PathVariable("id") Long id, Model model) {
        bRepository.deleteById(id);
        return "redirect:../addbudget";
    }

    @RequestMapping(value = "/result")
    public String showResult(Model model, Principal principal) {
        String username = principal.getName();
        AppUser currentUser = uRepository.findByUsername(username);

        List<Transaction> allTransactions;
        if (currentUser.getRole().equals("ADMIN")) {
            allTransactions = repository.findAll();
        } else {
            allTransactions = repository.findByUser(currentUser);
        }

        List<Budget> uBudgets;
        if (currentUser.getRole().equals("ADMIN")) {
            uBudgets = bRepository.findAll();
        } else {
            uBudgets = bRepository.findByUser(currentUser);
        }

        Set<String> allKeys = new HashSet<>();

        for (Transaction t : allTransactions) {
            allKeys.add(
                    t.getUser().getUsername() + "|" + t.getCategory().getName() + "|" + YearMonth.from(t.getDate()));
        }

        for (Budget budget : uBudgets) {
            allKeys.add(
                    budget.getUser().getUsername() + "|" + budget.getCategory().getName() + "|"
                            + budget.getBudgetDate());
        }

        List<FinancialResultDTO> result = new ArrayList<>();

        for (String key : allKeys) {
            String[] parts = key.split("\\|");
            String uName = parts[0];
            String cName = parts[1];
            String period = parts[2];

            double totalSpent = allTransactions.stream()
                    .filter(t -> t.getUser().getUsername().equals(uName) &&
                            t.getCategory().getName().equals(cName) &&
                            YearMonth.from(t.getDate()).toString().equals(period))
                    .mapToDouble(Transaction::getAmount).sum();

            double limit = uBudgets.stream()
                    .filter(b -> b.getUser().getUsername().equals(uName) &&
                            b.getCategory().getName().equals(cName) &&
                            b.getBudgetDate().toString().equals(period))
                    .map(Budget::getAmount).findFirst().orElse(0.0);

            result.add(new FinancialResultDTO(uName, cName, period, totalSpent, limit));
        }

        model.addAttribute("results", result);
        return "result";
    }

}
