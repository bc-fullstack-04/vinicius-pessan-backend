package br.com.sysmap.bootcamp.web;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/test")


public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello World! - I'm alive!";
    }


}