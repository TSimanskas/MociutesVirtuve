package Project.KodoKeliasLasProject.controller;

import Project.KodoKeliasLasProject.form.CommentForm;
import Project.KodoKeliasLasProject.form.RecipeForm;
import Project.KodoKeliasLasProject.service.ImageStorageService;
import Project.KodoKeliasLasProject.service.RecipeService;
import Project.KodoKeliasLasProject.service.RecipeService.RecipeDetailData;
import Project.KodoKeliasLasProject.service.RecipeService.RecipeListData;
import Project.KodoKeliasLasProject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final ImageStorageService imageStorageService;

    @GetMapping
    public String list() {
        return "recipes/list";
    }

    @GetMapping("/all")
    public String all(@RequestParam(required = false) String category,
                      @RequestParam(required = false) String search,
                      Model model) {
        RecipeListData data = recipeService.buildRecipeList(category, search);
        model.addAttribute("recipes", data.recipes());
        model.addAttribute("totalRecipes", data.totalRecipes());
        model.addAttribute("categoryCounts", data.categoryCounts());
        model.addAttribute("recipesByCategory", data.recipesByCategory());
        model.addAttribute("selectedCategory", data.selectedCategory());
        model.addAttribute("search", data.search());
        return "recipes/all";
    }

    @GetMapping("/new")
    public String create(Model model) {
        if (!model.containsAttribute("recipeForm")) {
            model.addAttribute("recipeForm", new RecipeForm());
        }

        model.addAttribute("categories", recipeService.getCategories());
        return "recipes/new";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute RecipeForm recipeForm,
                         BindingResult bindingResult,
                         Model model,
                         Principal principal) {
        imageStorageService.validateRecipeImage(recipeForm.getRecipeImage(), bindingResult);
        recipeService.validateCategory(recipeForm.getCategory(), bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", recipeService.getCategories());
            return "recipes/new";
        }

        var saved = recipeService.createRecipe(recipeForm, userService.requireUser(principal));
        return "redirect:/recipes/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, Principal principal) {
        RecipeDetailData detail = recipeService.buildRecipeDetail(id, userService.getUserOrNull(principal));
        model.addAttribute("recipe", detail.recipe());
        model.addAttribute("comments", detail.comments());
        model.addAttribute("canManageRecipe", detail.canManageRecipe());
        model.addAttribute("manageableCommentIds", detail.manageableCommentIds());

        if (!model.containsAttribute("commentForm")) {
            model.addAttribute("commentForm", new CommentForm());
        }

        return "recipes/view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        var recipe = recipeService.requireRecipeForManage(id, userService.requireUser(principal));

        if (!model.containsAttribute("recipeForm")) {
            model.addAttribute("recipeForm", recipeService.recipeToForm(recipe));
        }

        model.addAttribute("recipe", recipe);
        model.addAttribute("categories", recipeService.getCategories());
        return "recipes/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute RecipeForm recipeForm,
                       BindingResult bindingResult,
                       Model model,
                       Principal principal) {
        var recipe = recipeService.requireRecipeForManage(id, userService.requireUser(principal));

        imageStorageService.validateRecipeImage(recipeForm.getRecipeImage(), bindingResult);
        recipeService.validateCategory(recipeForm.getCategory(), bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("recipe", recipe);
            model.addAttribute("categories", recipeService.getCategories());
            return "recipes/edit";
        }

        recipeService.updateRecipe(id, recipeForm, userService.requireUser(principal));

        return "redirect:/recipes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        recipeService.deleteRecipe(id, userService.requireUser(principal));

        return "redirect:/recipes/all";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute CommentForm commentForm,
                             BindingResult bindingResult,
                             Model model,
                             Principal principal) {
        if (bindingResult.hasErrors()) {
            RecipeDetailData detail = recipeService.buildRecipeDetail(id, userService.getUserOrNull(principal));
            model.addAttribute("recipe", detail.recipe());
            model.addAttribute("comments", detail.comments());
            model.addAttribute("canManageRecipe", detail.canManageRecipe());
            model.addAttribute("manageableCommentIds", detail.manageableCommentIds());
            return "recipes/view";
        }

        recipeService.addComment(id, commentForm, userService.requireUser(principal));

        return "redirect:/recipes/" + id;
    }

    @GetMapping("/comments/{commentId}/edit")
    public String editComment(@PathVariable Long commentId, Model model, Principal principal) {
        var comment = recipeService.requireCommentForManage(commentId, userService.requireUser(principal));

        if (!model.containsAttribute("commentForm")) {
            model.addAttribute("commentForm", recipeService.commentToForm(comment));
        }

        model.addAttribute("comment", comment);
        return "comments/edit";
    }

    @PostMapping("/comments/{commentId}/edit")
    public String editComment(@PathVariable Long commentId,
                              @Valid @ModelAttribute CommentForm commentForm,
                              BindingResult bindingResult,
                              Model model,
                              Principal principal) {
        var comment = recipeService.requireCommentForManage(commentId, userService.requireUser(principal));

        if (bindingResult.hasErrors()) {
            model.addAttribute("comment", comment);
            return "comments/edit";
        }

        recipeService.updateComment(commentId, commentForm, userService.requireUser(principal));

        return "redirect:/recipes/" + comment.getRecipe().getId();
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Principal principal) {
        Long recipeId = recipeService.deleteComment(commentId, userService.requireUser(principal));

        return "redirect:/recipes/" + recipeId;
    }
}
