package cs.project.TextToSpeech.models;

import cs.project.TextToSpeech.infra.enums.UserPermission;
import cs.project.TextToSpeech.infra.enums.WorkspaceMemberStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "workspace_members")
public class WorkspaceMemberModel {
    @Id
    private String id;
    private String email;
    private String workspaceId;
    private UserPermission permission;
    private WorkspaceMemberStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
