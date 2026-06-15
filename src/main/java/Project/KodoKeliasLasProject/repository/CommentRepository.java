package Project.KodoKeliasLasProject.repository;

import Project.KodoKeliasLasProject.model.Comment;
import Project.KodoKeliasLasProject.model.Recipe;
import Project.KodoKeliasLasProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByRecipeOrderByCreatedAtAsc(Recipe recipe);

    long countByAuthor(User author);
}
