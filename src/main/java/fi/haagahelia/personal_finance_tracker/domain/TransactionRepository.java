package fi.haagahelia.personal_finance_tracker.domain;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    
     List<Transaction> findByDescription (String description);
}
