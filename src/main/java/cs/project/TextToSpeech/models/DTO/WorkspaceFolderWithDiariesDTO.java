package cs.project.TextToSpeech.models.DTO;

import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.WorkspaceDiaryFolderModel;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceFolderWithDiariesDTO {
    private WorkspaceDiaryFolderModel workspaceFolder;
    private List<DiaryModel> diaries;

    public WorkspaceFolderWithDiariesDTO(WorkspaceDiaryFolderModel workspaceFolder, List<DiaryModel> diaries) {
        this.workspaceFolder = workspaceFolder;
        this.diaries = diaries;
    }
}

