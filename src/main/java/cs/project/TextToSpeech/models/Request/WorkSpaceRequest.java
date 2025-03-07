package cs.project.TextToSpeech.models.Request;

import java.util.List;
import java.util.Map;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import lombok.Data;

@Data
public class WorkSpaceRequest {
    private String name;
    private String description;
    private Map<String, PermissionUser> members;
    private List<String> invitedMemberEmails;
}

