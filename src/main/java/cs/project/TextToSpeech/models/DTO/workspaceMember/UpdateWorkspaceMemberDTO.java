package cs.project.TextToSpeech.models.DTO.workspaceMember;

import cs.project.TextToSpeech.infra.enums.UserPermission;
import cs.project.TextToSpeech.infra.enums.WorkspaceMemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateWorkspaceMemberDTO {
    @NotNull(message = "Permission must not be null")
    private final UserPermission permission;

    UpdateWorkspaceMemberDTO(UserPermission permission) {
        this.permission = permission;
    }
}
