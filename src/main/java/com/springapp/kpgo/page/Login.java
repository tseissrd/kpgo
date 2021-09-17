package com.springapp.kpgo.page;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import com.springapp.kpgo.model.*;
import java.util.Base64;
import com.springapp.kpgo.security.AuthorizationManager;

// import com.springapp.kpgo.core.*;

import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.User;

@Controller
public class Login {
    
    @Autowired
    private DataRepository repository;
    
    @Autowired
    private AuthorizationManager authMgr;
    
    @RequestMapping("/login")
    public String login(@RequestHeader Map<String, String> headers) {
//        H2Adapter testAdapter = new H2Adapter();
//        testAdapter.test();
        test();
        String authHeader = headers.get("authorization");
        if (authHeader == null)
            return "login";
        else {
            System.out.println(authHeader);
            String[] authHeaderParts = authHeader.split(" ");
            String authMethod = authHeaderParts[0];
            String decodedAuthHeader;
            System.out.println(authMethod);
            try {
                decodedAuthHeader = new String(Base64.getDecoder().decode(authHeaderParts[1]), "UTF-8");
            } catch (Exception err) {
                System.err.println(err);
                return "login";
            }
            System.out.println(decodedAuthHeader);
            String[] authParts = decodedAuthHeader.split(":");
            if (authParts.length < 2)
                return "login";
            String username = authParts[0];
            String password = authParts[1];
            Password pwdObj = new Password(username, password);
            System.out.println(pwdObj.digest);
            User user = authMgr.authorize(username, pwdObj);
            if (user == null)
                return "login";
            System.out.println(user);
            
            Session session = authMgr.getSession(user);
            System.out.println(session.toString());
            
            return "login";
        }
    }
    
    public void test() {
        User myUser = repository.users().findByUsername("TestName");
        if (myUser == null) {
            Password password = new Password("TestName", "TestPwd");
            myUser = new User("TestName", password);
            repository.save(password);
            repository.save(myUser);
            System.out.println("created new user");
        }
        System.out.println(myUser);
    }
	
}