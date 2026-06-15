package Project.KodoKeliasLasProject.service;

import Project.KodoKeliasLasProject.form.RegisterForm;
import Project.KodoKeliasLasProject.model.Role;
import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterForm registerForm, BindingResult bindingResult) {
        validateRegistration(registerForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return;
        }

        User user = new User();
        user.setUsername(registerForm.getUsername().trim());
        user.setDisplayName(registerForm.getDisplayName().trim());
        user.setPasswordHash(passwordEncoder.encode(registerForm.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    private void validateRegistration(RegisterForm registerForm, BindingResult bindingResult) {
        String username = trimToEmpty(registerForm.getUsername());
        String password = trimToEmpty(registerForm.getPassword());
        String confirmPassword = trimToEmpty(registerForm.getConfirmPassword());

        if (!password.equals(confirmPassword)) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Slaptažodžiai nesutampa.");
        }

        if (!username.isBlank() && userRepository.existsByUsername(username)) {
            bindingResult.rejectValue("username", "username.exists", "Šis vartotojo vardas jau naudojamas.");
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
