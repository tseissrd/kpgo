package com.springapp.kpgo.pages;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Map;
import com.springapp.kpgo.models.Password;
import java.util.Base64;
import com.springapp.kpgo.core.Authorization;

// import com.springapp.kpgo.core.*;

import com.springapp.kpgo.repositories.UserRepository;
import com.springapp.kpgo.repositories.PasswordRepository;
import com.springapp.kpgo.models.User;

@Controller
public class Login {
    
    @Autowired
    private UserRepository myRepo;
    
    @Autowired
    private PasswordRepository pwdRepo;
    
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
            Password pwdObj = new Password(password);
            System.out.println(pwdObj.digest);
            User user = Authorization.getMgr().authorize(username, pwdObj);
            System.out.println(user);
            
            return "login";
        }
    }
    
    public void test() {
        User myUser = myRepo.findByUsername("TestName");
        if (myUser == null) {
            Password password = new Password("TestPwd");
            myUser = new User("TestName", password);
            pwdRepo.save(password);
            myRepo.save(myUser);
            System.out.println("created new user");
        }
        System.out.println(myUser);
    }
	
}