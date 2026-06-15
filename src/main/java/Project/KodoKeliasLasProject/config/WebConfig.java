package Project.KodoKeliasLasProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path profileUploadDirectory = Paths.get("uploads", "profile-images")
                .toAbsolutePath()
                .normalize();
        Path recipeUploadDirectory = Paths.get("uploads", "recipe-images")
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/uploads/profile-images/**")
                .addResourceLocations(profileUploadDirectory.toUri().toString());

        registry.addResourceHandler("/uploads/recipe-images/**")
                .addResourceLocations(recipeUploadDirectory.toUri().toString());
    }
}
