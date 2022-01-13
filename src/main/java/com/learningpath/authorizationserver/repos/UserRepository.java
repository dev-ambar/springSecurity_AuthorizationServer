package com.learningpath.authorizationserver.repos;

import com.learningpath.authorizationserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    public User findByEmail(String email);
}
