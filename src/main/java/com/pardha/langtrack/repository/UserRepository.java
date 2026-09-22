package com.pardha.langtrack.repository;

import com.pardha.langtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}