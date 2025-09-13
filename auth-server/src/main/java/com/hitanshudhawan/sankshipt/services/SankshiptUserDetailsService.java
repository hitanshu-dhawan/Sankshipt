package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SankshiptUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SankshiptUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                userOptional.get().getEmail(),
                userOptional.get().getPassword(),
                true,
                true,
                true,
                true,
                getAuthorities(userOptional.get().getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<Role> roles) {
        return roles.stream().map(role -> (GrantedAuthority) () -> "ROLE_" + role.getValue()).toList();
    }

}
