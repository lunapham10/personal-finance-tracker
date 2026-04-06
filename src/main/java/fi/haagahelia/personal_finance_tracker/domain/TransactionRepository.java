package fi.haagahelia.personal_finance_tracker.domain;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    
     @Override
    List<Transaction> findAll();
     List<Transaction> findByUser (AppUser user);
}
