package fi.haagahelia.personal_finance_tracker.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fi.haagahelia.personal_finance_tracker.domain.AppUser;
import fi.haagahelia.personal_finance_tracker.domain.AppUserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;

    public UserDetailServiceImpl(AppUserRepository uRepository) {
        this.repository = uRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        AppUser curruser = repository.findByUsername(username);
        if (curruser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        UserDetails user = new org.springframework.security.core.userdetails.User(username, 
            curruser.getPasswordHash(),
            AuthorityUtils.createAuthorityList(curruser.getRole()));

        return user;
    }
}
