package cs.project.TextToSpeech.models.DTO.workspace;

import cs.project.TextToSpeech.models.DTO.workspaceMember.WorkspaceMemberWithUserDTO;
import cs.project.TextToSpeech.models.WorkspaceModel;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceWithMembersDTO {
    public WorkspaceModel workspace;
    public List<WorkspaceMemberWithUserDTO> members;

    public WorkspaceWithMembersDTO(WorkspaceModel workSpace, List<WorkspaceMemberWithUserDTO> members) {
        this.workspace = workSpace;
        this.members = members;
    }
}
