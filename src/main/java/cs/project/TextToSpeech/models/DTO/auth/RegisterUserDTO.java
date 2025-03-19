package cs.project.TextToSpeech.models.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterUserDTO {
    @NotNull(message = "Name can not be null")
    @Size(max = 20, message = "Name must be less 20 characters long")
    private String name;

    @NotNull(message = "Email can not be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password can not be null")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
}
