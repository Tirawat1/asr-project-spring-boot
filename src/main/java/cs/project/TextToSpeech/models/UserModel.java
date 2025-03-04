package cs.project.TextToSpeech.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserModel {
    @Id
    private String id;

    private String name;
    private String email;
    private String password;

//    private List<String> diaryFolderIds;
}
