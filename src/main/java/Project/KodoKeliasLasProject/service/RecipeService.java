package Project.KodoKeliasLasProject.service;

import Project.KodoKeliasLasProject.form.CommentForm;
import Project.KodoKeliasLasProject.form.RecipeForm;
import Project.KodoKeliasLasProject.model.Comment;
import Project.KodoKeliasLasProject.model.Recipe;
import Project.KodoKeliasLasProject.model.Role;
import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.repository.CommentRepository;
import Project.KodoKeliasLasProject.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private static final List<String> CATEGORIES = List.of(
            "Desertai",
            "Gėrimai",
            "Kepiniai",
            "Padažai",
            "Pagrindiniai patiekalai",
            "Pusryčiai",
            "Salotos",
            "Sriubos",
            "Užkandžiai",
            "Kita"
    );

    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ImageStorageService imageStorageService;

    public List<String> getCategories() {
        return CATEGORIES;
    }

    public RecipeListData buildRecipeList(String category, String search) {
        List<Recipe> allRecipes = recipeRepository.findAllByOrderByCreatedAtDesc();
        List<Recipe> searchedRecipes = filterRecipesBySearch(allRecipes, search);
        List<Recipe> recipes = filterRecipesByCategory(searchedRecipes, category);
        Map<String, Long> categoryCounts = countRecipesByCategory(searchedRecipes);
        Map<String, List<Recipe>> recipesByCategory = groupRecipesByCategory(recipes);

        return new RecipeListData(
                recipes,
                searchedRecipes.size(),
                categoryCounts,
                recipesByCategory,
                category,
                search
        );
    }

    public Recipe createRecipe(RecipeForm recipeForm, User author) {
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeForm.getTitle().trim());
        recipe.setDescription(trimToNull(recipeForm.getDescription()));
        recipe.setCategory(recipeForm.getCategory());
        recipe.setIngredients(recipeForm.getIngredients().trim());
        recipe.setInstructions(recipeForm.getInstructions().trim());
        if (recipeForm.getRecipeImage() != null && !recipeForm.getRecipeImage().isEmpty()) {
            recipe.setImageUrl(imageStorageService.saveRecipeImage(recipeForm.getRecipeImage()));
        }
        recipe.setAuthor(author);

        return recipeRepository.save(recipe);
    }

    public RecipeDetailData buildRecipeDetail(Long id, User currentUserOrNull) {
        Recipe recipe = requireRecipe(id);
        List<Comment> comments = commentRepository.findByRecipeOrderByCreatedAtAsc(recipe);

        return new RecipeDetailData(
                recipe,
                comments,
                canManage(recipe.getAuthor(), currentUserOrNull),
                comments.stream()
                        .filter(comment -> canManage(comment.getAuthor(), currentUserOrNull))
                        .map(Comment::getId)
                        .toList()
        );
    }

    public Recipe requireRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    public Recipe requireRecipeForManage(Long id, User currentUser) {
        Recipe recipe = requireRecipe(id);
        requireCanManage(recipe.getAuthor(), currentUser);
        return recipe;
    }

    public void updateRecipe(Long id, RecipeForm recipeForm, User currentUser) {
        Recipe recipe = requireRecipeForManage(id, currentUser);
        updateRecipeFromForm(recipe, recipeForm);
        if (recipeForm.getRecipeImage() != null && !recipeForm.getRecipeImage().isEmpty()) {
            recipe.setImageUrl(imageStorageService.saveRecipeImage(recipeForm.getRecipeImage()));
        }
        recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id, User currentUser) {
        Recipe recipe = requireRecipeForManage(id, currentUser);
        recipeRepository.delete(recipe);
    }

    public RecipeForm recipeToForm(Recipe recipe) {
        RecipeForm recipeForm = new RecipeForm();
        recipeForm.setTitle(recipe.getTitle());
        recipeForm.setDescription(recipe.getDescription());
        recipeForm.setCategory(recipe.getCategory());
        recipeForm.setIngredients(recipe.getIngredients());
        recipeForm.setInstructions(recipe.getInstructions());
        return recipeForm;
    }

    public void addComment(Long recipeId, CommentForm commentForm, User author) {
        Recipe recipe = requireRecipe(recipeId);

        Comment comment = new Comment();
        comment.setText(commentForm.getText().trim());
        comment.setRecipe(recipe);
        comment.setAuthor(author);
        commentRepository.save(comment);
    }

    public Comment requireComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"));
    }

    public Comment requireCommentForManage(Long commentId, User currentUser) {
        Comment comment = requireComment(commentId);
        requireCanManage(comment.getAuthor(), currentUser);
        return comment;
    }

    public CommentForm commentToForm(Comment comment) {
        CommentForm commentForm = new CommentForm();
        commentForm.setText(comment.getText());
        return commentForm;
    }

    public void updateComment(Long commentId, CommentForm commentForm, User currentUser) {
        Comment comment = requireCommentForManage(commentId, currentUser);
        comment.setText(commentForm.getText().trim());
        commentRepository.save(comment);
    }

    public Long deleteComment(Long commentId, User currentUser) {
        Comment comment = requireCommentForManage(commentId, currentUser);
        Long recipeId = comment.getRecipe().getId();
        commentRepository.delete(comment);
        return recipeId;
    }

    private List<Recipe> filterRecipesBySearch(List<Recipe> recipes, String search) {
        if (search == null || search.isBlank()) {
            return recipes;
        }

        String normalizedSearch = normalizeSearchText(search);

        return recipes.stream()
                .filter(recipe -> containsNormalized(recipe.getTitle(), normalizedSearch))
                .toList();
    }

    private boolean containsNormalized(String value, String normalizedSearch) {
        return value != null && normalizeSearchText(value).contains(normalizedSearch);
    }

    private String normalizeSearchText(String value) {
        String lowerCaseValue = value.toLowerCase(Locale.ROOT).trim();
        return Normalizer.normalize(lowerCaseValue, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private List<Recipe> filterRecipesByCategory(List<Recipe> recipes, String category) {
        if (category == null || category.isBlank()) {
            return recipes;
        }

        return recipes.stream()
                .filter(recipe -> getCategory(recipe).equalsIgnoreCase(category))
                .toList();
    }

    private Map<String, Long> countRecipesByCategory(List<Recipe> recipes) {
        Map<String, Long> categoryCounts = new LinkedHashMap<>();
        CATEGORIES.forEach(category -> categoryCounts.put(category, 0L));

        recipes.forEach(recipe -> {
            String category = getCategory(recipe);
            categoryCounts.putIfAbsent(category, 0L);
            categoryCounts.put(category, categoryCounts.get(category) + 1);
        });

        return categoryCounts;
    }

    private Map<String, List<Recipe>> groupRecipesByCategory(List<Recipe> recipes) {
        Map<String, List<Recipe>> recipesByCategory = new LinkedHashMap<>();

        recipes.forEach(recipe -> {
            String category = getCategory(recipe);

            recipesByCategory.computeIfAbsent(category, key -> new ArrayList<>())
                    .add(recipe);
        });

        return recipesByCategory;
    }

    private String getCategory(Recipe recipe) {
        String category = recipe.getCategory();
        return category == null || category.isBlank() ? "Kita" : category;
    }

    private void updateRecipeFromForm(Recipe recipe, RecipeForm recipeForm) {
        recipe.setTitle(recipeForm.getTitle().trim());
        recipe.setDescription(trimToNull(recipeForm.getDescription()));
        recipe.setCategory(recipeForm.getCategory());
        recipe.setIngredients(recipeForm.getIngredients().trim());
        recipe.setInstructions(recipeForm.getInstructions().trim());
    }

    private void requireCanManage(User author, User currentUser) {
        if (!canManage(author, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot manage this content");
        }
    }

    private boolean canManage(User author, User currentUser) {
        if (author == null || currentUser == null) {
            return false;
        }

        return currentUser.getRole() == Role.ADMIN || currentUser.getId().equals(author.getId());
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public record RecipeListData(
            List<Recipe> recipes,
            int totalRecipes,
            Map<String, Long> categoryCounts,
            Map<String, List<Recipe>> recipesByCategory,
            String selectedCategory,
            String search
    ) {
    }

    public record RecipeDetailData(
            Recipe recipe,
            List<Comment> comments,
            boolean canManageRecipe,
            List<Long> manageableCommentIds
    ) {
    }
}
