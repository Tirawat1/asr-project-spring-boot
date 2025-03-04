package cs.project.TextToSpeech.models;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("personalDiaryFolder")
public class PersonalDiaryFolderModel extends DiaryFolderModel{
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
