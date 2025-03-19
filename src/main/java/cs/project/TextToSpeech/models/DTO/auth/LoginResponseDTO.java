package cs.project.TextToSpeech.models.DTO.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private long expiresIn;
}