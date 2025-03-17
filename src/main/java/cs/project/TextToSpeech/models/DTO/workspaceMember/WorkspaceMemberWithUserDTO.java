package cs.project.TextToSpeech.models.DTO.workspaceMember;

import cs.project.TextToSpeech.models.UserModel;
import cs.project.TextToSpeech.models.WorkspaceMemberModel;
import lombok.Getter;

@Getter
public class WorkspaceMemberWithUserDTO {
    private final WorkspaceMemberModel workspaceMember;
    private final UserModel user;

    public WorkspaceMemberWithUserDTO(WorkspaceMemberModel workspaceMember, UserModel user) {
        this.workspaceMember = workspaceMember;
        this.user = user;
    }
}
