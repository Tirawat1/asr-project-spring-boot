package cs.project.TextToSpeech.models.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagRequest {
    @NotNull(message = "Tag name cannot be null")
    @NotEmpty(message = "Tag name cannot be empty")
    private String tagName;

    private String colorCode;

    @NotNull(message = "Owner id cannot be null")
    @NotEmpty(message = "Owner id cannot be empty")
    private String ownerId;
}
