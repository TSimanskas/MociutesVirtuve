package Project.KodoKeliasLasProject.repository;

import Project.KodoKeliasLasProject.model.Recipe;
import Project.KodoKeliasLasProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r JOIN FETCH r.author ORDER BY r.createdAt DESC")
    List<Recipe> findAllWithAuthorByOrderByCreatedAtDesc();

    List<Recipe> findByAuthorOrderByCreatedAtDesc(User author);

    long countByAuthor(User author);

    @Query("SELECT r FROM Recipe r JOIN FETCH r.author WHERE r.id = :id")
    Optional<Recipe> findByIdWithAuthor(Long id);
}
