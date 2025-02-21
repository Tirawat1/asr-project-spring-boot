package cs.project.TextToSpeech.models;

import lombok.Data;
import java.util.List;

@Data
public class DiaryRequest {
    private DiaryModel diary;
    private List<TagModel> tags;
}
