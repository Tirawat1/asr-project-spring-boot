package cs.project.TextToSpeech.models.Request;

import java.util.List;
import java.util.Map;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import cs.project.TextToSpeech.models.WorkspaceIcon;
import lombok.Data;

@Data
public class WorkSpaceRequest {
    private String name;
    private String description;
    private WorkspaceIcon icon;
    private Map<String, PermissionUser> members;
    private List<String> invitedMemberEmails;
}

