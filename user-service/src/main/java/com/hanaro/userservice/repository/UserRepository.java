package com.hanaro.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.userservice.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

  boolean existsByPhone(String phone);
}
