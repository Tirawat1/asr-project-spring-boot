package cs.project.TextToSpeech.models.Request;

import java.util.Map;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkSpaceRequest {
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    private String description;
    private Map<String , PermissionUser> members; // key: email, value: role
}

