package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.repository.TagRepository;
import cs.project.TextToSpeech.models.TagModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    // Create a new tag
    public TagModel createTag(TagModel tag) {
        return tagRepository.save(tag);
    }

    // get tag by  ID
    public TagModel getTagById(String id) {
        return tagRepository.findById(id).orElse(null);  
    }
}
