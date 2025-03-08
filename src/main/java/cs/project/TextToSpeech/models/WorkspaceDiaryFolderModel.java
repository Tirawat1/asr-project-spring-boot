package cs.project.TextToSpeech.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("workspaceDiaryFolder")
public class WorkspaceDiaryFolderModel extends DiaryFolderModel {
    private String workspaceId;
}
