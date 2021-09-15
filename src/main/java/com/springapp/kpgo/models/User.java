/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 *
 * @author Sovereign
 */
@Entity
public class User {
    
    public User() {
    }
    
    public User(String name, String password) {
        this.username = name;
        this.password = password;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty
    private String username;
    
    @NotEmpty
    private String password;
    
    @Override
    public String toString() {
        return username + ":***";
    }
    
}
