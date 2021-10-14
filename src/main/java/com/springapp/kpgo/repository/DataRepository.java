/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.springapp.kpgo.repository.*;
import java.util.HashMap;
import org.springframework.data.jpa.repository.JpaRepository;
import com.springapp.kpgo.model.*;
import java.util.Map;

/**
 *
 * @author Sovereign
 */
@Component
public class DataRepository
//        implements JpaRepository<Object, Object>
{

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordRepository passwordRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private ContentRepository contentRepository;
    
    private boolean initialized;
    
    private static final Map<String, JpaRepository> repositories = new HashMap<>();
    
    public DataRepository() {
        this.initialized = false;
        System.out.println("created new DataRepository");
    }
    
    private void init(Class entryClass, JpaRepository repository) {
        String className = entryClass.getCanonicalName();
        if (!repositories.containsKey(className))
            repositories.put(className, repository);
    }
    
    private void init() {
        if (this.initialized)
          return;
        init(User.class, userRepository);
        init(Password.class, passwordRepository);
        init(Session.class, sessionRepository);
        init(Resource.class, resourceRepository);
        init(Content.class, contentRepository);
        this.initialized = true;
    }
    
    public <S> S save(S entity) {
      init();
      String className = entity.getClass().getCanonicalName();
      JpaRepository repository = repositories.get(className);
      if (repository == null) {
        System.err.println("Could not find repository for " + className);
        return null;
      }
      return (S)repository.save(entity);
    }
    
    public <S> boolean delete(S entity) {
      init();
      String className = entity.getClass().getCanonicalName();
      JpaRepository repository = repositories.get(className);
      if (repository == null) {
        System.err.println("Could not find repository for " + className);
        return false;
      }
      repository.delete(entity);
      return true;
    }
    
    public UserRepository users() {
      return userRepository;
    }
    
    public SessionRepository sessions() {
      return sessionRepository;
    }
    
    public ResourceRepository resources() {
      return resourceRepository;
    }
    
}
