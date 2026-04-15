package com.hanaro.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.userservice.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
}
