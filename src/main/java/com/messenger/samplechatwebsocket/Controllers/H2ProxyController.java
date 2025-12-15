package com.messenger.samplechatwebsocket.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class H2ProxyController {


    @GetMapping({"/admin/h2", "/admin/h2/"})
    public String redirectToH2() {
        return "redirect:/h2-console/";

}
}