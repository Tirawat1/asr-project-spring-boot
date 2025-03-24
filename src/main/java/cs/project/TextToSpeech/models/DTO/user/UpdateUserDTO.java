package cs.project.TextToSpeech.models.DTO.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateUserDTO {
    private final String name;
    private final MultipartFile profileImgPath;

    public UpdateUserDTO(String name, MultipartFile profileImgPath) {
        this.name = name;
        this.profileImgPath = profileImgPath;
    }
}
