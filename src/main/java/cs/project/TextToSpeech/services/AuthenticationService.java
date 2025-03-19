package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.models.DTO.auth.LoginUserDTO;
import cs.project.TextToSpeech.models.DTO.auth.RegisterUserDTO;
import cs.project.TextToSpeech.models.UserModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public UserModel signup(RegisterUserDTO registerUserDTO) {
        return userService.createUser(registerUserDTO);
    }

    public UserModel authenticate(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}