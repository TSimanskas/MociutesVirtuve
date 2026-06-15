package Project.KodoKeliasLasProject.service;

import Project.KodoKeliasLasProject.form.RegisterForm;
import Project.KodoKeliasLasProject.model.Role;
import Project.KodoKeliasLasProject.model.User;
import Project.KodoKeliasLasProject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_savesUserWhenFormIsValid() {
        RegisterForm form = validForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registerForm");

        when(userRepository.existsByUsername("tomas")).thenReturn(false);
        when(passwordEncoder.encode("secret12")).thenReturn("encoded-password");

        authService.register(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isFalse();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("tomas");
        assertThat(savedUser.getDisplayName()).isEqualTo("Tomas");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void register_rejectsMismatchedPasswords() {
        RegisterForm form = validForm();
        form.setConfirmPassword("different");
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registerForm");

        authService.register(form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("confirmPassword")).isTrue();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_rejectsExistingUsername() {
        RegisterForm form = validForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registerForm");

        when(userRepository.existsByUsername("tomas")).thenReturn(true);

        authService.register(form, bindingResult);

        assertThat(bindingResult.hasFieldErrors("username")).isTrue();
        verify(userRepository, never()).save(any(User.class));
    }

    private RegisterForm validForm() {
        RegisterForm form = new RegisterForm();
        form.setUsername("tomas");
        form.setDisplayName("Tomas");
        form.setPassword("secret12");
        form.setConfirmPassword("secret12");
        return form;
    }
}
