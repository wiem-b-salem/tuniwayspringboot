package com.tuniway.controller;


import com.tuniway.model.*;
import com.tuniway.model.enums.PlaceCategory;
import com.tuniway.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TourPersonnaliseService tourService;

    @Autowired
    private ReservationService reservationService;

    // Check if user is admin
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole().toString().equals("ADMIN");
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalPlaces", placeService.getAllPlaces().size());
        model.addAttribute("totalReviews", reviewService.getAllReviews().size());
        model.addAttribute("totalTours", tourService.getAllTours().size());
        model.addAttribute("totalReservations", reservationService.getAllReservations().size());

        return "admin/dashboard";
    }

    @GetMapping("/places")
    public String places(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("places", placeService.getAllPlaces());
        model.addAttribute("categories", PlaceCategory.values());
        return "admin/places";
    }

    @PostMapping("/places/create")
    public String createPlace(@ModelAttribute Place place,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        placeService.createPlace(place);
        redirectAttributes.addFlashAttribute("success", "Place created successfully!");
        return "redirect:/admin/places";
    }

    @PostMapping("/places/update/{id}")
    public String updatePlace(@PathVariable Long id,
                              @ModelAttribute Place place,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        place.setId(id);
        placeService.updatePlace(place);
        redirectAttributes.addFlashAttribute("success", "Place updated successfully!");
        return "redirect:/admin/places";
    }

    @PostMapping("/places/delete/{id}")
    public String deletePlace(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        try {
            placeService.deletePlace(id);
            redirectAttributes.addFlashAttribute("success", "Place deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete place with existing reviews");
        }
        return "redirect:/admin/places";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete user with existing data");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/reviews")
    public String reviews(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");
        return "redirect:/admin/reviews";
    }

    @GetMapping("/tours")
    public String tours(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("tours", tourService.getAllTours());
        return "admin/tours";
    }

    @PostMapping("/tours/delete/{id}")
    public String deleteTour(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }

        tourService.deleteTour(id);
        redirectAttributes.addFlashAttribute("success", "Tour deleted successfully!");
        return "redirect:/admin/tours";
    }
}