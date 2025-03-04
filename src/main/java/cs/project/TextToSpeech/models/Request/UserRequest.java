package cs.project.TextToSpeech.models.Request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
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
