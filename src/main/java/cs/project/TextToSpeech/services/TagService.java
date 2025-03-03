package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.repository.TagRepository;
import cs.project.TextToSpeech.models.TagModel;
import cs.project.TextToSpeech.models.Request.TagRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    // Create a new tag
    public TagModel createEntry(TagRequest request) {

        try{
            if(request.getName() == null || request.getName().trim().isEmpty()){
                throw new IllegalArgumentException("Tag name cannot be empty");
            }

            Optional<TagModel> existingTag = tagRepository.findByName(request.getName());

            if (existingTag.isPresent()) {
                throw new IllegalArgumentException("Tag with name " + request.getName() + " already exists");
            }

            TagModel tag = new TagModel();
            tag.setTagName(request.getName());

            if (request.getColorCode() == null || request.getColorCode().isEmpty()) {
                tag.setColorCode("E4E0E1");
            } else {
                tag.setColorCode(request.getColorCode());
            }

            return tagRepository.save(tag);
            }catch (IllegalArgumentException e){
                throw new RuntimeException("Validation failed: " + e.getMessage());

            }
    }

    public TagModel updateEntry(@PathVariable String id, TagRequest request) {

        try{
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
             TagModel tag = tagRepository.findById(id).orElseThrow(NoSuchElementException::new);

        // check name
        if (!tag.getTagName().equals(request.getName())) {
            Optional<TagModel> existingTag = tagRepository.findByName(request.getName());
            if (existingTag.isPresent() && !existingTag.get().getTagsIds().equals(id)) {
                throw new IllegalArgumentException("Tag with name " + request.getName() + " already exists");
            }

            tag.setTagName(request.getName());
        }

        if (request.getColorCode() == null || request.getColorCode().isEmpty()) {
            tag.setColorCode("E4E0E1");
        } else {
            tag.setColorCode(request.getColorCode());
        }

        return tagRepository.save(tag);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Error updating tag: " + e.getMessage());
        }
    }

    public void deleteEntry(@PathVariable String id) {
        try {
            tagRepository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Tag with id " + id + " not found");
        }
    }

    // get tag by  ID
    public TagModel getTagById(String id) {
        return tagRepository.findById(id).orElse(null);  
    }

    public List<TagModel> getAllEntries() {
        return tagRepository.findAll();
    }

    public List<TagModel> getTagsByIds(List<String> tagIds) {
        List<TagModel> tags = new ArrayList<>();
        for (String tagId : tagIds) {
            TagModel tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new NoSuchElementException("Tag with id " + tagId + " not found"));

            tags.add(tag);
        }
        return tags;
    }
}
