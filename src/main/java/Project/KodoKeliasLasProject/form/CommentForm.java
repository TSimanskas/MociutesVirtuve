package Project.KodoKeliasLasProject.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @NotBlank(message = "Komentaras negali būti tuščias.")
    @Size(max = 1000, message = "Komentaras negali būti ilgesnis nei 1000 simbolių.")
    private String text;
}
