package cs.project.TextToSpeech.models.DTO.workspace;

import cs.project.TextToSpeech.models.WorkspaceIcon;
import lombok.Data;

@Data
public class UpdateWorkspaceDTO {
    private final String name;
    private final String description;
    private final WorkspaceIcon icon;

    UpdateWorkspaceDTO(String name, String description, WorkspaceIcon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}
