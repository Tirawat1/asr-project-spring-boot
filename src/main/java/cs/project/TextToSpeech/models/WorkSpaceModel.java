package cs.project.TextToSpeech.models;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import lombok.Data;

@Data
@Document(collection = "workspaces")
public class WorkSpaceModel {
    @Id
    private String workspaceId;

    private String workspaceName;
    private String description;
    private String ownerId;
//    private List<String> folderDiaryIds;
    private Map<String, PermissionUser> members;
}
