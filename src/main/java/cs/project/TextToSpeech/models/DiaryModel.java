package cs.project.TextToSpeech.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "asr_thai_backend")
public class DiaryModel {
    @Id
    private String id;
    private String title;
    private List<Map<String, Object>> content;
    private Instant createdAt;
    private Instant updatedAt;

    @DBRef
    private List<TagModel> tags;  // Reference to tags
}
