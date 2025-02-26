package cs.project.TextToSpeech.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "diaries")
public class DiaryModel {
    @Id
    @MongoId
    private String id;

    private String title;
    private List<Map<String, Object>> content;

    @DBRef
    private List<TagModel> tags;  // Reference to tags

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
