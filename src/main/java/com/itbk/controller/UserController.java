package com.itbk.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    // Controller for Home page
    @RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
    public String homePage(Model model) {
        model.addAttribute("title", "This is Home page");

        return "home";
    }

    // Controller for Admin
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model) {
        model.addAttribute("title", "This is Admin page");

        return "/admin/admin";
    }

    // Controller for teacher
    @RequestMapping(value = "/teacher", method = RequestMethod.GET)
    public String teacherPage(Model model) {
        model.addAttribute("title", "This is Teacher page");

        return "/teacher/teacher";
    }

    // Controller for student
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public String studentPage(Model model) {
        model.addAttribute("title", "This is Student page");

        return "/student/student";
    }

    // Controller for the login
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("title", "This is Login page");

        return "login";
    }

    // Controller for customize the error message
    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username and password!";
        }

        return error;
    }

    // Controller for 403 access denied page
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accesssDeniedPage(Model model) {
        // check if user is logining
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            System.out.println(userDetail);
            model.addAttribute("username", userDetail.getUsername());
        }

        return "403";
    }
}