package cs.project.TextToSpeech.models.Request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {

    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Password cannot be null")
    private String password;

    @NotNull(message = "Name cannot be null")
    private String name;

    private String email;
    private String role;
    
    private List<String> workspaceIds;
}
