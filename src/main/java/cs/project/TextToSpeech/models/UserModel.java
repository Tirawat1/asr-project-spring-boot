package cs.project.TextToSpeech.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserModel {
    @Id
    @MongoId
    private String id;

    private String username;
    private String password;

    private String name;
    private String email;
    private String role;
    
    private List<String> workspaceIds;
}
