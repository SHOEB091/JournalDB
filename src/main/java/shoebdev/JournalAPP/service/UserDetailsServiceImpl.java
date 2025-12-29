package shoebdev.JournalAPP.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



public class UserDetailsServiceImpl implements UserDetailsService {
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return null;
    }

}
