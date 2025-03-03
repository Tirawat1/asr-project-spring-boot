package cs.project.TextToSpeech.models.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagRequest {
    @NotNull(message = "tagName cannot be null")
    @NotEmpty(message = "tagName cannot be empty")
    private String tagName;

    private String colorCode;
}
