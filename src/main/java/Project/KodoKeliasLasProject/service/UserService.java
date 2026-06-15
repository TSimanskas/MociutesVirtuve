package Project.KodoKeliasLasProject.service;

import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User requireUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return requireUser(principal.getName());
    }

    public User requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
    }

    public User getUserOrNull(Principal principal) {
        if (principal == null) {
            return null;
        }

        return findByUsername(principal.getName()).orElse(null);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String getProfileName(User user) {
        if (user.getDisplayName() == null || user.getDisplayName().isBlank()) {
            return user.getUsername();
        }

        return user.getDisplayName();
    }
}
