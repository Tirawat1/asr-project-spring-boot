package cs.project.TextToSpeech.models;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import cs.project.TextToSpeech.infra.enums.PermissionUser;
import lombok.Data;

@Data
@Document(collection = "workspaces")
public class WorkSpaceModel {
    @Id
    @MongoId
    private String id;

    private String name;
    private String description;
    private String ownerId;
    private List<String> diaryList; 

    private Map<String,PermissionUser> members; 
}
