package com.test.ChatApplicaation.security;

import com.test.ChatApplicaation.entity.User;
import com.test.ChatApplicaation.exceptions.ResourceNotFoundException;
import com.test.ChatApplicaation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityUtil implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserSecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User", "email" + username, 0));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

    }
}
