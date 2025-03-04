package cs.project.TextToSpeech.models;

import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "diary_folders")
public class DiaryFolderModel {
    @Id
    private String diaryFolderId;
    private String folderName;
    private List<String> diaryIds;

    // @DBRef
    // private DiaryFolderModel parentFolder;

    // @DBRef
    // private List<DiaryFolderModel> subFolders;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private Instant updatedAt;
}
