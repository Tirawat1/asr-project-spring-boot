package cs.project.TextToSpeech.models;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeAlias("personalTag")
public class PersonalTagModel extends TagModel {
    private String userId;
}
