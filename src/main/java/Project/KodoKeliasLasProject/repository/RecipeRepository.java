package Project.KodoKeliasLasProject.repository;

import Project.KodoKeliasLasProject.model.Recipe;
import Project.KodoKeliasLasProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByOrderByCreatedAtDesc();

    List<Recipe> findByAuthorOrderByCreatedAtDesc(User author);

    long countByAuthor(User author);
}
