package fi.haagahelia.personal_finance_tracker.web;

import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.domain.TransactionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {
    private final TransactionRepository repository;

    public TransactionRestController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Transaction> getTransactions() {
        return repository.findAll();
    }
}
