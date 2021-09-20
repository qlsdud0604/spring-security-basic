package com.example.springsecuritybasic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller   // View를 리턴하겠다는 애너테이션
public class IndexController {

    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }
}
