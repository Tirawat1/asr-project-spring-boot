package cs.project.TextToSpeech.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "workspaces")
public class WorkspaceModel {
    @Id
    private String id;
    private String name;
    private String description;
    private WorkspaceIcon icon;
}
