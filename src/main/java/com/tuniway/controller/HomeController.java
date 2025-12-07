package com.tuniway.controller;

import com.tuniway.model.*;
import com.tuniway.model.enums.RoleType;
import com.tuniway.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("places", placeService.getAllPlaces());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        return "index";
    }

    @GetMapping("/explore")
    public String explore(Model model) {
        model.addAttribute("places", placeService.getAllPlaces());
        model.addAttribute("categories", new String[]{"HISTORICAL", "BEACH", "RESTAURANT", "HOTEL", "MUSEUM", "NATURE", "SHOPPING", "ENTERTAINMENT"});
        return "places/explorer";
    }

    @GetMapping("/tours")
    public String tours(Model model) {
        // Add tours data
        return "tours/list";
    }

    // ==================== LOGIN ====================
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String message,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        try {
            // Find user by email using UserService method
            Optional<User> userOpt = userService.getUserByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Simple password check (TODO: Use BCrypt in production)
                if (user.getPassword().equals(password)) {
                    // Login successful - redirect based on RoleType
                    model.addAttribute("currentUser", user);

                    if (user.getRole() == RoleType.ADMIN) {
                        return "redirect:/admin";
                    } else if (user.getRole() == RoleType.GUIDE) {
                        return "redirect:/guide/profile";
                    } else if (user.getRole() == RoleType.CLIENT) {
                        return "redirect:/profile";
                    }

                    return "redirect:/profile"; // default
                }
            }

            // Login failed
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";
        }
    }

    // ==================== REGISTER ====================
    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String error,
                               @RequestParam(required = false) String message,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String role,
                           RedirectAttributes redirectAttributes) {
        try {
            // Check if email already exists
            if (userService.existsByEmail(email)) {
                redirectAttributes.addAttribute("error", "Email already registered");
                return "redirect:/register";
            }

            // Check if username already exists
            if (userService.existsByUsername(username)) {
                redirectAttributes.addAttribute("error", "Username already taken");
                return "redirect:/register";
            }

            // Create new user based on role
            User newUser;

            if ("GUIDE".equals(role)) {
                newUser = new Guide();
            } else if ("ADMIN".equals(role)) {
                newUser = new Admin();
            } else {
                newUser = new Client(); // Default to CLIENT
            }

            // Set common fields
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // TODO: Hash with BCrypt in production

            // Save user
            userService.createUser(newUser);

            redirectAttributes.addAttribute("message", "Registration successful! Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // ==================== PROFILE PAGES ====================
    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO: Get current logged-in user from session
        // model.addAttribute("user", currentUser);
        return "user/profile";
    }

    @GetMapping("/guide/profile")
    public String guideProfile(Model model) {
        // TODO: Get current logged-in guide from session
        // model.addAttribute("guide", currentGuide);
        return "guide/profile";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalPlaces", placeService.getAllPlaces().size());
        return "admin/dashboard";
    }
}