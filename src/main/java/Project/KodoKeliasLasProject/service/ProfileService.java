package Project.KodoKeliasLasProject.service;

import Project.KodoKeliasLasProject.form.ProfileForm;
import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.repository.CommentRepository;
import Project.KodoKeliasLasProject.repository.RecipeRepository;
import Project.KodoKeliasLasProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ImageStorageService imageStorageService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public ProfilePageData buildProfilePageData(User user) {
        return new ProfilePageData(
                user,
                userService.getProfileName(user),
                recipeRepository.countByAuthor(user),
                commentRepository.countByAuthor(user)
        );
    }

    public void updateProfile(User user, ProfileForm profileForm) {
        user.setDisplayName(profileForm.getDisplayName().trim());
        if (profileForm.getProfileImage() != null && !profileForm.getProfileImage().isEmpty()) {
            String previousImageUrl = user.getProfileImageUrl();
            user.setProfileImageUrl(imageStorageService.saveProfileImage(profileForm.getProfileImage()));
            imageStorageService.deleteStoredImage(previousImageUrl);
        }
        userRepository.save(user);
    }

    public record ProfilePageData(
            User user,
            String profileName,
            long recipeCount,
            long commentCount
    ) {
    }
}
