package cs.project.TextToSpeech.models.DTO;

import cs.project.TextToSpeech.models.WorkSpaceModel;
import cs.project.TextToSpeech.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetWorkSpaceByUserIdDto {
    private WorkSpaceModel workspace;
    private UserModel owner;
    private List<UserModel> members;
}
