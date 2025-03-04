package cs.project.TextToSpeech.models.DTO;

import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.PersonalDiaryFolderModel;
import cs.project.TextToSpeech.models.WorkspaceDiaryFolderModel;
import lombok.Data;

import java.util.List;

@Data
public class PersonalFolderWithDiariesDTO {
    private PersonalDiaryFolderModel personalFolder;
    private List<DiaryModel> diaries;

    public PersonalFolderWithDiariesDTO(PersonalDiaryFolderModel personalFolder, List<DiaryModel> diaries) {
        this.personalFolder = personalFolder;
        this.diaries = diaries;
    }
}

