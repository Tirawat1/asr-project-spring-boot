package cs.project.TextToSpeech.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "tags")
public class TagModel {
    @Id
    @MongoId
    private String tagsIds;

    private String tagName;
    private String colorCode;
}
