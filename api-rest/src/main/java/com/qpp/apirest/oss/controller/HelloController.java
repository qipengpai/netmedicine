package com.qpp.apirest.oss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author qipengpai
 * @Date 2018/10/25 17:25
 * @Version 1.0.1
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello rest";
    }
}
