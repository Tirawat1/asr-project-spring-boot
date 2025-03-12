package cs.project.TextToSpeech.models.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagRequest {
    @NotEmpty(message = "Tag name cannot be empty")
    private String tagName;
    private String colorCode;
}
