package cs.project.TextToSpeech.models;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import lombok.Data;

@Data
@Document(collection = "workspaces")
public class WorkSpaceModel {
    @Id
    private String id;

    private String name;
    private String description;

    private Map<String, PermissionUser> members; // email , permission (owner, editor, viewer)
}
