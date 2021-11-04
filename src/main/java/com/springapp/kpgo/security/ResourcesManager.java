/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.security;

import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.User;
import com.springapp.kpgo.model.Password;
import com.springapp.kpgo.model.Resource;
import com.springapp.kpgo.model.Session;
import com.springapp.kpgo.model.Content;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.NoSuchElementException;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sovereign
 */
@Component
public class ResourcesManager {
    
  @Autowired
  private DataRepository repository;

  @Autowired
  private AuthorizationManager authMgr;

  private ResourcesManager() {
    System.out.println("created new resources mgr");
  }
  
  public Resource getResource(Long id)
  throws NoSuchElementException
  {
    return repository.resources().findById(id).get();
  }

  public void giveAccess(Resource resource, User user) {
    System.out.println("will give access for " + resource.getId() + " to " + user.getUsername());
    resource.giveAccess(user);
    repository.save(resource);
  }
  
  public void giveAccess(Resource resource, Collection<User> users) {
    resource.giveAccess(users);
    repository.save(resource);
  }
  
  public void denyAccess(Resource resource, User user) {
    resource.denyAccess(user);
    repository.save(resource);
  }
  
  public boolean checkAccess(Resource resource, User user) {
    return resource.checkAccess(user);
  }
  
  public <T extends Serializable> Resource<T> writeContent(Resource<T> resource, T object) {
    Content<T> content = resource.getContent();
    if (content != null) {
      content.write(object);
    } else {
      content = new Content();
      content.write(object);
      resource.setContent(content);
    }
    repository.save(content);
    return (Resource<T>)repository.save(resource);
  }
  
  public <T extends Serializable> T readContent(Resource<T> resource) {
    return resource.getContent().read();
  }
    
}
