package com.ecom.userservice.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecom.userservice.constants.UserServiceConstants;
import com.ecom.userservice.repository.UserAccountRepository;

@Service
public class UserAccountDetailsService implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;

	public UserAccountDetailsService(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userAccountRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(UserServiceConstants.USER_NOT_FOUND_BY_USERNAME_MESSAGE));
        var authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(UserServiceConstants.ROLE_PREFIX + r.getName().name()))
                .toList();
		return new User(user.getUsername(), user.getPasswordHash(), authorities);
	}
}


