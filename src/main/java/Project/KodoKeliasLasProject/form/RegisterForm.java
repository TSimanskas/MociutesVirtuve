package Project.KodoKeliasLasProject.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "Vartotojo vardas yra privalomas.")
    @Size(min = 3, max = 50, message = "Vartotojo vardas turi būti nuo 3 iki 50 simbolių.")
    private String username;

    @NotBlank(message = "Rodomas vardas yra privalomas.")
    @Size(max = 80, message = "Rodomas vardas negali būti ilgesnis nei 80 simbolių.")
    private String displayName;

    @NotBlank(message = "Slaptažodis yra privalomas.")
    @Size(min = 6, message = "Slaptažodis turi būti bent 6 simbolių.")
    private String password;

    @NotBlank(message = "Pakartokite slaptažodį.")
    private String confirmPassword;
}
