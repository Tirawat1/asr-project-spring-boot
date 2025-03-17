package cs.project.TextToSpeech.models.DTO;

import cs.project.TextToSpeech.models.WorkspaceModel;
import cs.project.TextToSpeech.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkspaceWithUsersDTO {
    private WorkspaceModel workspace;
    private List<UserModel> members;
}
