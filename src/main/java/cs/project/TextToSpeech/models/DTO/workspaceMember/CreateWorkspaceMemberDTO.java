package cs.project.TextToSpeech.models.DTO.workspaceMember;

import cs.project.TextToSpeech.infra.enums.UserPermission;
import cs.project.TextToSpeech.infra.enums.WorkspaceMemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateWorkspaceMemberDTO {
    @NotNull(message = "Email must not be null")
    @Email(message = "Email must be email")
    private final String email;

    @NotNull(message = "Permission must not be null")
    private final UserPermission permission;

    private final WorkspaceMemberStatus status;

    CreateWorkspaceMemberDTO(String email, UserPermission permission, WorkspaceMemberStatus status) {
        this.email = email;
        this.permission = permission;
        this.status = status == null ? WorkspaceMemberStatus.PENDING : status;
    }
}
