package com.mcnc.assetmgmt.welcome.controller;

import com.mcnc.assetmgmt.util.common.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
/**
 * title : WelcomeController
 *
 * description : 웰컴페이지
 *
 * reference :
 *
 * author : 임현영
 * date : 2023.11.28
 **/
@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping("")
    public String WelcomePage(){
        return "hi";
    }


}
