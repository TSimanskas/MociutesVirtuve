package Project.KodoKeliasLasProject.repository;

import Project.KodoKeliasLasProject.model.Comment;
import Project.KodoKeliasLasProject.model.Recipe;
import Project.KodoKeliasLasProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.recipe = :recipe ORDER BY c.createdAt ASC")
    List<Comment> findByRecipeWithAuthorOrderByCreatedAtAsc(Recipe recipe);

    long countByAuthor(User author);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.recipe WHERE c.id = :id")
    Optional<Comment> findByIdWithAuthorAndRecipe(Long id);
}
