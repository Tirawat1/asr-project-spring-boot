package cs.project.TextToSpeech.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
    private List<String> members;
    private List<String> diaryList; 
}
