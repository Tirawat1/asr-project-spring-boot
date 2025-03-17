package cs.project.TextToSpeech.models.DTO.workspace;

import cs.project.TextToSpeech.models.DTO.workspaceMember.CreateWorkspaceMemberDTO;
import cs.project.TextToSpeech.models.WorkspaceIcon;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateWorkspaceDTO {
    @NotNull(message = "name must not be null")
    @NotEmpty(message =  "name must not be empty")
    private final String name;

    private final String description;

    private final WorkspaceIcon icon;

    private final List<CreateWorkspaceMemberDTO> members;

    CreateWorkspaceDTO(String name,
                       String description,
                       WorkspaceIcon icon,
                       List<CreateWorkspaceMemberDTO> members
    ) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.members = members;
    }
}
