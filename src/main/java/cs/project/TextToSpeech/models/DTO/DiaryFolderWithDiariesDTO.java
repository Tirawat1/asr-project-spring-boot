package cs.project.TextToSpeech.models.DTO;

import cs.project.TextToSpeech.models.DiaryFolderModel;
import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.PersonalDiaryFolderModel;
import lombok.Data;

import java.util.List;

@Data
public class DiaryFolderWithDiariesDTO {
    private DiaryFolderModel diaryFolderModel;
    private List<DiaryModel> diaries;

    public DiaryFolderWithDiariesDTO(DiaryFolderModel diaryFolderModel, List<DiaryModel> diaries) {
        this.diaryFolderModel = diaryFolderModel;
        this.diaries = diaries;
    }
}

