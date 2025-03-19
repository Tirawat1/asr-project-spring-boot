package cs.project.TextToSpeech.controller;

import cs.project.TextToSpeech.models.DTO.auth.LoginResponseDTO;
import cs.project.TextToSpeech.models.DTO.auth.LoginUserDTO;
import cs.project.TextToSpeech.models.DTO.auth.RegisterUserDTO;
import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.services.AuthenticationService;
import cs.project.TextToSpeech.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserModel> register(@RequestBody RegisterUserDTO registerUserDto) {
        UserModel registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginUserDTO loginUserDto) {
        UserModel authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserModel> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserModel currentUser = (UserModel) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }
}
