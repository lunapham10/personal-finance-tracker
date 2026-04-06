package fi.haagahelia.personal_finance_tracker.domain;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BudgetRepository extends CrudRepository<Budget, Long>{
    List<Budget>findByCategory (Category category);
    Budget findByCategoryAndBudgetDate (Category category, LocalDate budgetDate);
    List<Budget> findByUser(AppUser user);
    @Override
    List<Budget> findAll();
    Budget findByUserAndCategoryAndBudgetDate(AppUser user, String category, YearMonth budgetDate);
}
