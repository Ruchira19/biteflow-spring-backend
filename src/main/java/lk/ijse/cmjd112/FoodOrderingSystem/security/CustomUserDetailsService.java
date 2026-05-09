package lk.ijse.cmjd112.FoodOrderingSystem.security;

import lk.ijse.cmjd112.FoodOrderingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user authentication details from the database during the authentication process.

 * Spring Security uses this service during:
 *     User login authentication
 *     JWT token validation
 *     Authentication context creation
 * Users are authenticated using their email address.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository used to retrieve user details from the database.
     */
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }
}