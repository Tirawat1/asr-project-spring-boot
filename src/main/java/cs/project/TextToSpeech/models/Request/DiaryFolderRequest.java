package cs.project.TextToSpeech.models.Request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiaryFolderRequest {
    @NotNull(message = "FolderName cannot be null")
    @NotEmpty(message = "FolderName cannot be empty")
    private String folderName;

    private List<String> diaryIds;
}
