package Project.KodoKeliasLasProject.controller;

import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserService userService;

    @ModelAttribute("currentDisplayName")
    public String currentDisplayName(Principal principal) {
        return getCurrentUser(principal)
                .map(userService::getProfileName)
                .orElse(principal == null ? null : principal.getName());
    }

    @ModelAttribute("currentProfileImageUrl")
    public String currentProfileImageUrl(Principal principal) {
        return getCurrentUser(principal)
                .map(User::getProfileImageUrl)
                .filter(profileImageUrl -> !profileImageUrl.isBlank())
                .orElse(null);
    }

    @ModelAttribute("currentProfileInitial")
    public String currentProfileInitial(Principal principal) {
        String displayName = currentDisplayName(principal);

        if (displayName == null || displayName.isBlank()) {
            return "V";
        }

        return displayName.substring(0, 1).toUpperCase();
    }

    private Optional<User> getCurrentUser(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }

        return userService.findByUsername(principal.getName());
    }
}
