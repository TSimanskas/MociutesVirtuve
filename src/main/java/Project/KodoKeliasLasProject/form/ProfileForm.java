package Project.KodoKeliasLasProject.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileForm {

    @NotBlank(message = "Vardas yra privalomas.")
    @Size(max = 80, message = "Vardas negali būti ilgesnis nei 80 simbolių.")
    private String displayName;

    private MultipartFile profileImage;
}
