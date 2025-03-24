package cs.project.TextToSpeech.models.DTO.user;

import cs.project.TextToSpeech.models.UserModel;
import lombok.Getter;

@Getter
public class UserWithImageUrl {
    private final UserModel user;
    private final String imageUrl;

    public UserWithImageUrl(UserModel user, String imageUrl) {
        this.user = user;
        this.imageUrl = imageUrl;
    }
}
