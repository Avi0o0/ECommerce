package com.ecom.userservice.bootstrap;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecom.userservice.entity.Role;
import com.ecom.userservice.entity.RoleName;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.repository.RoleRepository;
import com.ecom.userservice.repository.UserAccountRepository;

@Component
public class AdminSeeder implements ApplicationRunner {

	private final UserAccountRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;

	public AdminSeeder(UserAccountRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (userRepo.existsByUsername("admin")) {
			return;
		}
		Role adminRole = roleRepo.findByName(RoleName.ADMIN).orElseGet(() -> {
			Role r = new Role();
			r.setName(RoleName.ADMIN);
			return roleRepo.save(r);
		});
		UserAccount admin = new UserAccount();
		admin.setUsername("admin");
		admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
		admin.setRoles(Set.of(adminRole));
		userRepo.save(admin);
	}
}


