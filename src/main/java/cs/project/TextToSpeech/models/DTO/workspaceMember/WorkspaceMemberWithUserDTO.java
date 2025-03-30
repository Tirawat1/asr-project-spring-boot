package cs.project.TextToSpeech.models.DTO.workspaceMember;

import cs.project.TextToSpeech.models.DTO.user.UserWithImageUrl;
import cs.project.TextToSpeech.models.WorkspaceMemberModel;
import lombok.Getter;

@Getter
public class WorkspaceMemberWithUserDTO {
    private final WorkspaceMemberModel workspaceMember;
    private final UserWithImageUrl userWithImageUrl;

    public WorkspaceMemberWithUserDTO(WorkspaceMemberModel workspaceMember, UserWithImageUrl userWithImageUrl) {
        this.workspaceMember = workspaceMember;
        this.userWithImageUrl = userWithImageUrl;
    }
}
