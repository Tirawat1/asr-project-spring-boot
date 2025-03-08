package cs.project.TextToSpeech.models.DTO;

import java.util.List;

import cs.project.TextToSpeech.models.DiaryModel;
import cs.project.TextToSpeech.models.PersonalTagModel;
import lombok.Data;

@Data
public class PersonalTagDto {
    private PersonalTagModel personalTag;

    public PersonalTagDto(PersonalTagModel personalTag) {
        this.personalTag = personalTag;
    }
}
