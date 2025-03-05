package cs.project.TextToSpeech.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("personalDiaryFolder")
public class PersonalDiaryFolderModel extends DiaryFolderModel{
    private String userId;
}
