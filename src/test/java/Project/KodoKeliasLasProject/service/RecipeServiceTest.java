package Project.KodoKeliasLasProject.service;

import Project.KodoKeliasLasProject.form.RecipeForm;
import Project.KodoKeliasLasProject.model.Role;
import Project.KodoKeliasLasProject.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private Project.KodoKeliasLasProject.repository.RecipeRepository recipeRepository;

    @Mock
    private Project.KodoKeliasLasProject.repository.CommentRepository commentRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private RecipeService recipeService;

    private User owner;
    private User otherUser;
    private User admin;

    @BeforeEach
    void setUp() {
        owner = user(1L, Role.USER);
        otherUser = user(2L, Role.USER);
        admin = user(3L, Role.ADMIN);
    }

    @Test
    void canManage_allowsOwner() {
        assertThat(recipeService.canManage(owner, owner)).isTrue();
    }

    @Test
    void canManage_allowsAdmin() {
        assertThat(recipeService.canManage(owner, admin)).isTrue();
    }

    @Test
    void canManage_deniesOtherUsers() {
        assertThat(recipeService.canManage(owner, otherUser)).isFalse();
    }

    @Test
    void validateCategory_acceptsKnownCategory() {
        RecipeForm form = new RecipeForm();
        form.setCategory("Desertai");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "recipeForm");

        recipeService.validateCategory(form.getCategory(), bindingResult);

        assertThat(bindingResult.hasFieldErrors("category")).isFalse();
    }

    @Test
    void validateCategory_rejectsUnknownCategory() {
        RecipeForm form = new RecipeForm();
        form.setCategory("Unknown");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "recipeForm");

        recipeService.validateCategory(form.getCategory(), bindingResult);

        assertThat(bindingResult.hasFieldErrors("category")).isTrue();
    }

    @Test
    void getCategories_includesKonservavimas() {
        assertThat(recipeService.getCategories()).contains("Konservavimas");
    }

    private User user(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername("user" + id);
        user.setPasswordHash("hash");
        user.setRole(role);
        return user;
    }
}
