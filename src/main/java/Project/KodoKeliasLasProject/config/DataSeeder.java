package Project.KodoKeliasLasProject.config;

import Project.KodoKeliasLasProject.model.Recipe;
import Project.KodoKeliasLasProject.model.Role;
import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.repository.RecipeRepository;
import Project.KodoKeliasLasProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        User admin = userRepository.findByUsername("admin")
                .orElseGet(this::createAdminUser);
        List<Recipe> existingRecipes = new ArrayList<>(recipeRepository.findAll());

        saveRecipeIfMissing(existingRecipes, createChocolateChipCookies(admin), "Chocolate Chip Cookies");
        saveRecipeIfMissing(existingRecipes, createGrandmaCake(admin), "Simple Homemade Cake");
        saveRecipeIfMissing(existingRecipes, createColdBeetrootSoup(admin), "Lithuanian Cold Beetroot Soup");
    }

    private void saveRecipeIfMissing(List<Recipe> existingRecipes, Recipe recipe, String previousTitle) {
        Recipe existingRecipe = existingRecipes.stream()
                .filter(savedRecipe -> hasSameTitle(savedRecipe, recipe.getTitle())
                        || hasSameTitle(savedRecipe, previousTitle))
                .findFirst()
                .orElse(null);

        if (existingRecipe == null) {
            existingRecipes.add(recipeRepository.save(recipe));
            return;
        }

        existingRecipe.setTitle(recipe.getTitle());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setCategory(recipe.getCategory());
        existingRecipe.setIngredients(recipe.getIngredients());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setImageUrl(recipe.getImageUrl());
        existingRecipe.setAuthor(recipe.getAuthor());
        recipeRepository.save(existingRecipe);
    }

    private boolean hasSameTitle(Recipe recipe, String title) {
        return recipe.getTitle().equalsIgnoreCase(title);
    }

    private User createAdminUser() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setDisplayName("Tomas");
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);

        return userRepository.save(admin);
    }

    private Recipe createChocolateChipCookies(User author) {
        Recipe recipe = new Recipe();
        recipe.setTitle("Sausainiai su šokolado gabaliukais");
        recipe.setDescription("Minkšti sviestiniai sausainiai su apskrudusiais krašteliais ir daug šokolado gabaliukų.");
        recipe.setCategory("Desertai");
        recipe.setIngredients("""
                225 g minkšto sviesto
                150 g rudojo cukraus
                100 g baltojo cukraus
                2 kiaušiniai
                1 a. š. vanilės ekstrakto
                300 g kvietinių miltų
                1 a. s. sodos
                1/2 a. š. druskos
                250 g šokolado gabaliukų
                """);
        recipe.setInstructions("""
                Įkaitinkite orkaitę iki 180°C ir kepimo skardą išklokite kepimo popieriumi.
                Sviestą išplakite su ruduoju ir baltuoju cukrumi, kol masė taps puri.
                Įmuškite kiaušinius ir supilkite vanilės ekstraktą.
                Suberkite miltus, sodą ir druską, tada viską trumpai išmaišykite.
                Įmaišykite šokolado gabaliukus.
                Šaukštu dėkite tešlą ant skardos ir kepkite 10-12 minučių.
                Prieš patiekdami leiskite sausainiams atvėsti.
                """);
        recipe.setAuthor(author);
        return recipe;
    }

    private Recipe createGrandmaCake(User author) {
        Recipe recipe = new Recipe();
        recipe.setTitle("Paprastas naminis pyragas");
        recipe.setDescription("Minkštas kasdienis pyragas, labai tinkantis prie arbatos, uogų ar šaukšto uogienės.");
        recipe.setCategory("Desertai");
        recipe.setIngredients("""
                3 kiaušiniai
                180 g cukraus
                120 ml pieno
                120 ml aliejaus
                250 g kvietinių miltų
                2 a. š. kepimo miltelių
                1 a. š. vanilės ekstrakto
                Žiupsnelis druskos
                Cukraus pudros patiekimui
                """);
        recipe.setInstructions("""
                Įkaitinkite orkaitę iki 180°C ir patepkite kepimo formą riebalais.
                Kiaušinius išplakite su cukrumi, kol masė taps šviesi ir puri.
                Supilkite pieną, aliejų ir vanilės ekstraktą.
                Suberkite miltus, kepimo miltelius ir druską, tada išmaišykite.
                Tešlą supilkite į paruoštą kepimo formą.
                Kepkite 30-35 minutes, kol įsmeigtas medinis pagaliukas bus sausas.
                Pyragą atvėsinkite ir prieš patiekdami apibarstykite cukraus pudra.
                """);
        recipe.setAuthor(author);
        return recipe;
    }

    private Recipe createColdBeetrootSoup(User author) {
        Recipe recipe = new Recipe();
        recipe.setTitle("Lietuviški šaltibarščiai");
        recipe.setDescription("Gaivi lietuviška vasaros sriuba iš kefyro, burokėlių, agurkų, krapų ir virtų kiaušinių.");
        recipe.setCategory("Sriubos");
        recipe.setIngredients("""
                500 g virtų burokėlių, sutarkuotų
                1 agurkas, supjaustytas kubeliais
                4 svogūnų laiškai, susmulkinti
                Sauja šviežių krapų, susmulkintų
                1 l kefyro
                2 virti kiaušiniai
                Druskos pagal skonį
                Virtų bulvių patiekimui
                """);
        recipe.setInstructions("""
                Į didelį dubenį sudėkite tarkuotus burokėlius, agurką, svogūnų laiškus ir krapus.
                Supilkite kefyrą ir gerai išmaišykite.
                Pagardinkite druska pagal skonį.
                Sriubą bent 30 minučių palaikykite šaldytuve.
                Patiekite šaltus barščius su virtų kiaušinių puselėmis.
                Šalia patiekite karštų virtų bulvių.
                """);
        recipe.setAuthor(author);
        return recipe;
    }
}
