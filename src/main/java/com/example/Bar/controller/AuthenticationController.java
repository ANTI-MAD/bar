package com.example.Bar.controller;

import com.example.Bar.dto.authentication.SignInRequestDTO;
import com.example.Bar.dto.authentication.SignUpRequestDTO;
import com.example.Bar.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody final SignUpRequestDTO signUpRequestDTO){
        authenticationService.signUp(signUpRequestDTO);
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public String signIn(@RequestBody final SignInRequestDTO signInRequestDTO){
        return authenticationService.signIn(signInRequestDTO);
    }
}
