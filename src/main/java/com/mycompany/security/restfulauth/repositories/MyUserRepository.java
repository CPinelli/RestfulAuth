package com.mycompany.security.restfulauth.repositories;

import com.mycompany.security.restfulauth.entities.MyUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Integer> {

    MyUser findByUsername(String username);

    MyUser findByEmail(String email);

    List<MyUser> findAllByIsActiveIsTrue();
}