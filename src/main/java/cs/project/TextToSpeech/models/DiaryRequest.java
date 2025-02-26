package cs.project.TextToSpeech.models;

import lombok.Data;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class DiaryRequest {
    private DiaryModel diary;

    @DBRef
    private List<TagModel> tags;
}
