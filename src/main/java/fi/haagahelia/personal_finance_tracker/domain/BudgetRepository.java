package fi.haagahelia.personal_finance_tracker.domain;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BudgetRepository extends CrudRepository<Budget, Long>{
    List<Budget>findByCategory (Category category);
    Budget findByCategoryAndBudgetDate (Category category, LocalDate budgetDate);
}
