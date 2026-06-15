package Project.KodoKeliasLasProject.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Path RECIPE_IMAGE_DIRECTORY = Paths.get("uploads", "recipe-images");
    private static final Path PROFILE_IMAGE_DIRECTORY = Paths.get("uploads", "profile-images");
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_RECIPE_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024;

    public void validateRecipeImage(MultipartFile recipeImage, BindingResult bindingResult) {
        if (recipeImage == null || recipeImage.isEmpty()) {
            return;
        }

        String extension = getExtension(recipeImage);

        if (recipeImage.getSize() > MAX_RECIPE_IMAGE_SIZE) {
            bindingResult.rejectValue("recipeImage", "recipeImage.tooLarge",
                    "Recepto nuotrauka negali būti didesnė nei 5 MB.");
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            bindingResult.rejectValue("recipeImage", "recipeImage.invalid",
                    "Įkelkite JPG, PNG, GIF arba WEBP nuotrauką.");
        }
    }

    public String saveRecipeImage(MultipartFile recipeImage) {
        try {
            Files.createDirectories(RECIPE_IMAGE_DIRECTORY);

            String extension = getExtension(recipeImage);
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = RECIPE_IMAGE_DIRECTORY.resolve(fileName)
                    .toAbsolutePath()
                    .normalize();

            Files.copy(recipeImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/recipe-images/" + fileName;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save recipe image", exception);
        }
    }

    public void validateProfileImage(MultipartFile profileImage, BindingResult bindingResult) {
        if (profileImage == null || profileImage.isEmpty()) {
            return;
        }

        String extension = getExtension(profileImage);

        if (profileImage.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            bindingResult.rejectValue("profileImage", "profileImage.tooLarge",
                    "Profilio nuotrauka negali būti didesnė nei 5 MB.");
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            bindingResult.rejectValue("profileImage", "profileImage.invalid",
                    "Įkelkite JPG, PNG, GIF arba WEBP nuotrauką.");
        }
    }

    public String saveProfileImage(MultipartFile profileImage) {
        try {
            Files.createDirectories(PROFILE_IMAGE_DIRECTORY);

            String extension = getExtension(profileImage);
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = PROFILE_IMAGE_DIRECTORY.resolve(fileName)
                    .toAbsolutePath()
                    .normalize();

            Files.copy(profileImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/profile-images/" + fileName;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save profile image", exception);
        }
    }

    private String getExtension(MultipartFile image) {
        String fileName = image.getOriginalFilename();

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
