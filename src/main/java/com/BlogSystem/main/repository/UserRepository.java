package com.BlogSystem.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BlogSystem.main.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User  findByEmail(String email);
	User  findByUsername(String username);
}
