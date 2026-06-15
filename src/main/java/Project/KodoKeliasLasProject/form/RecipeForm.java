package Project.KodoKeliasLasProject.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RecipeForm {

    @NotBlank(message = "Recepto pavadinimas yra privalomas.")
    @Size(max = 150, message = "Pavadinimas negali būti ilgesnis nei 150 simbolių.")
    private String title;

    @Size(max = 500, message = "Aprašymas negali būti ilgesnis nei 500 simbolių.")
    private String description;

    @NotBlank(message = "Pasirinkite kategoriją.")
    private String category;

    @NotBlank(message = "Ingredientai yra privalomi.")
    private String ingredients;

    @NotBlank(message = "Gaminimo eiga yra privaloma.")
    private String instructions;

    private MultipartFile recipeImage;
}
