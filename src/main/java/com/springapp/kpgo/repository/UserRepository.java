/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springapp.kpgo.model.User;

/**
 *
 * @author Sovereign
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
    User findByUsername(String name);
    
}
