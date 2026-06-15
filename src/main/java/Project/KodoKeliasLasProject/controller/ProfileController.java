package Project.KodoKeliasLasProject.controller;

import Project.KodoKeliasLasProject.form.ProfileForm;
import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.service.ImageStorageService;
import Project.KodoKeliasLasProject.service.ProfileService;
import Project.KodoKeliasLasProject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final ImageStorageService imageStorageService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        var user = userService.requireUser(principal);

        if (!model.containsAttribute("profileForm")) {
            ProfileForm profileForm = new ProfileForm();
            profileForm.setDisplayName(userService.getProfileName(user));
            model.addAttribute("profileForm", profileForm);
        }

        addProfileAttributes(model, user);
        return "profile/view";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute ProfileForm profileForm,
                                BindingResult bindingResult,
                                Model model,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        var user = userService.requireUser(principal);

        imageStorageService.validateProfileImage(profileForm.getProfileImage(), bindingResult);

        if (bindingResult.hasErrors()) {
            addProfileAttributes(model, user);
            return "profile/view";
        }

        profileService.updateProfile(user, profileForm);

        redirectAttributes.addFlashAttribute("profileUpdated", true);
        return "redirect:/profile";
    }

    private void addProfileAttributes(Model model, User user) {
        var data = profileService.buildProfilePageData(user);
        model.addAttribute("user", data.user());
        model.addAttribute("profileName", data.profileName());
        model.addAttribute("recipeCount", data.recipeCount());
        model.addAttribute("commentCount", data.commentCount());
    }
}
