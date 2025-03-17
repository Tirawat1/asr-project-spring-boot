package cs.project.TextToSpeech.infra.repository;

import cs.project.TextToSpeech.models.WorkspaceMemberModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceMemberRepository extends MongoRepository<WorkspaceMemberModel, String> {
    @Query("{ 'email': ?0 }")
    List<WorkspaceMemberModel> findByEmail(String email);
    @Query("{ 'workspaceId': ?0 }")
    List<WorkspaceMemberModel> findByWorkspaceId(String workspaceId);

    void deleteByWorkspaceId(String workspaceId);
}
