package com.example.proj.capstone.Controller;

import com.example.proj.capstone.Model.User;
import com.example.proj.capstone.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepo userRepo;


    @PostMapping("/signup")
    public String signup(@RequestBody User user)throws MessagingException, UnsupportedEncodingException{
       if(!userRepo.existsById(user.getEmail())){
           BCryptPasswordEncoder bcrypt=new BCryptPasswordEncoder();
           String encryptpassword= bcrypt.encode(user.getPassword());
           user.setPassword(encryptpassword);
           userRepo.save(user);
           return "Verification mail sent";
       }
       else{
           Optional<User> userDetails=userRepo.findById(user.getEmail());
           if(userDetails.get().getEnabled()){
               return "Already exists";
           } else{
               return "waiting for verification";
           }
       }
    }
}
