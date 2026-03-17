package fi.haagahelia.personal_finance_tracker;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fi.haagahelia.personal_finance_tracker.domain.Category;
import fi.haagahelia.personal_finance_tracker.domain.CategoryRepository;
import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.domain.TransactionRepository;
import fi.haagahelia.personal_finance_tracker.domain.AppUser;
import fi.haagahelia.personal_finance_tracker.domain.AppUserRepository;

@SpringBootApplication
public class PersonalFinanceTrackerApplication {
	private static final Logger log = LoggerFactory.getLogger(PersonalFinanceTrackerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PersonalFinanceTrackerApplication.class, args);
	}

	@Bean
	public CommandLineRunner trackerDemo(TransactionRepository repository, CategoryRepository cRepository, AppUserRepository uRepository) {
		return (args) -> {
			log.info("save a couple of transactions");
			cRepository.save(new Category("Food"));
			cRepository.save(new Category("Rent"));
			cRepository.save(new Category("Salary"));
			cRepository.save(new Category("Transport"));
			cRepository.save(new Category("Shopping"));
			
			// private String description;
    		// private LocalDate date;
    		// private Double amount;
    		// private String type;

			repository.save(new Transaction("Breakfast", LocalDate.of(2026, 3, 1), 11.90, "Expense", cRepository.findByName("Food").get(0)));
			
			AppUser user1 = new AppUser("user", "$2a$06$3jYRJrg0ghaaypjZ/.g4SethoeA51ph3UD4kZi9oPkeMTpjKU5uo6", "user@gmail.com","USER");
			AppUser user2 = new AppUser("admin", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "admin@gmail.com","ADMIN");
			uRepository.save(user1);
			uRepository.save(user2);
			
			log.info("fetch all books");
			for (Transaction transaction : repository.findAll()) {
				log.info(transaction.toString());
			}
		};
	}
}
