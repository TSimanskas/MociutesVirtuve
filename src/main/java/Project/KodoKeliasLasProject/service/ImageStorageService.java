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
    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private static final long MAX_RECIPE_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024;

    public void validateRecipeImage(MultipartFile recipeImage, BindingResult bindingResult) {
        validateImage(recipeImage, "recipeImage", MAX_RECIPE_IMAGE_SIZE,
                "Recepto nuotrauka negali būti didesnė nei 5 MB.",
                "Įkelkite JPG, PNG, GIF arba WEBP nuotrauką.",
                bindingResult);
    }

    public String saveRecipeImage(MultipartFile recipeImage) {
        return saveImage(recipeImage, RECIPE_IMAGE_DIRECTORY, "/uploads/recipe-images/");
    }

    public void validateProfileImage(MultipartFile profileImage, BindingResult bindingResult) {
        validateImage(profileImage, "profileImage", MAX_PROFILE_IMAGE_SIZE,
                "Profilio nuotrauka negali būti didesnė nei 5 MB.",
                "Įkelkite JPG, PNG, GIF arba WEBP nuotrauką.",
                bindingResult);
    }

    public String saveProfileImage(MultipartFile profileImage) {
        return saveImage(profileImage, PROFILE_IMAGE_DIRECTORY, "/uploads/profile-images/");
    }

    public void deleteStoredImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank() || !imageUrl.startsWith("/uploads/")) {
            return;
        }

        Path imagePath = Paths.get("." + imageUrl).normalize().toAbsolutePath();

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not delete image", exception);
        }
    }

    private void validateImage(MultipartFile image,
                               String fieldName,
                               long maxSize,
                               String tooLargeMessage,
                               String invalidMessage,
                               BindingResult bindingResult) {
        if (image == null || image.isEmpty()) {
            return;
        }

        String extension = getExtension(image);
        String contentType = image.getContentType();

        if (image.getSize() > maxSize) {
            bindingResult.rejectValue(fieldName, fieldName + ".tooLarge", tooLargeMessage);
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)
                || contentType == null
                || !ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            bindingResult.rejectValue(fieldName, fieldName + ".invalid", invalidMessage);
        }
    }

    private String saveImage(MultipartFile image, Path directory, String publicUrlPrefix) {
        try {
            Files.createDirectories(directory);

            String extension = getExtension(image);
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = directory.resolve(fileName)
                    .toAbsolutePath()
                    .normalize();

            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return publicUrlPrefix + fileName;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not save image", exception);
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
