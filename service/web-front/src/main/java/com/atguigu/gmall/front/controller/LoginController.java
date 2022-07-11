package com.atguigu.gmall.front.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fanyudong
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String login(@RequestParam(value = "originUrl",defaultValue = "ttp://www.gmall.com")
                        String originUrl, Model model){

        model.addAttribute("originUrl",originUrl);

        return "login";
    }



}
