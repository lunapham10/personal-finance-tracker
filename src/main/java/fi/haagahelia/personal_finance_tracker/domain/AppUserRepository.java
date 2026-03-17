package fi.haagahelia.personal_finance_tracker.domain;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser, Long>{
    AppUser findByUsername(String username);
}