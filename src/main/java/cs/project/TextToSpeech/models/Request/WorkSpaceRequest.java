package cs.project.TextToSpeech.models.Request;

import java.util.List;
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

    @NotNull(message = "ownerId cannot be null")
    @NotEmpty(message = "ownerId cannot be empty")
    private String ownerId;

    private Map<String , PermissionUser> members; // key: userId, value: role
    private List<String> diaryList;
}
