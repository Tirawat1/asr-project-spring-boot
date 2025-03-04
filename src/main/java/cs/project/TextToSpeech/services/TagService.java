package cs.project.TextToSpeech.services;

import cs.project.TextToSpeech.infra.repository.TagRepository;
import cs.project.TextToSpeech.infra.repository.UserRepository;
import cs.project.TextToSpeech.models.TagModel;
import cs.project.TextToSpeech.models.Request.TagRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    // Create a new tag
    public TagModel createEntry(TagRequest request) {
        try {
            List<TagModel> tagModels = getAllEntriesByOwnerId(request.getOwnerId());

            boolean isDuplicate = tagModels.stream()
                    .anyMatch(tag -> tag.getTagName().equalsIgnoreCase(request.getTagName()));

            if (isDuplicate) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Tag with name " + request.getTagName() + " already exists");
            }

            TagModel tag = new TagModel();
            tag.setOwnerId(request.getOwnerId());
            tag.setTagName(request.getTagName());
            tag.setColorCode(
                    (request.getColorCode() == null || request.getColorCode().isEmpty()) ? "E4E0E1" : request.getColorCode());

            return tagRepository.save(tag);
        } catch (Exception e) {
            throw new RuntimeException("Error creating tag: " + e.getMessage());
        }
    }


    public List<TagModel> getAllEntriesByOwnerId(String ownerId) {
        try {
            Objects.requireNonNull(ownerId, "OwnerId cannot be null");

            if (!userRepository.existsById(ownerId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OwnerId not found");
            }

            return tagRepository.getAllEntriesByOwnerId(ownerId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public TagModel updateEntry(String id, TagRequest request) {
        try {
            Objects.requireNonNull(id, "ID cannot be null");

            if (!userRepository.existsById(request.getOwnerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OwnerId not found");
            }

            TagModel tagModel = tagRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag with ID " + id + " not found"));

            List<TagModel> tagModels = getAllEntriesByOwnerId(request.getOwnerId());

            Optional<TagModel> existingTag = tagModels.stream()
                    .filter(tag -> tag.getTagName().equals(request.getTagName()) && !tag.getTagId().equals(id))
                    .findFirst();

            if (existingTag.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag with name " + request.getTagName() + " already exists");
            }

            tagModel.setTagName(request.getTagName());
            tagModel.setOwnerId(request.getOwnerId());
            tagModel.setColorCode((request.getColorCode() == null || request.getColorCode().isEmpty()) ? "E4E0E1" : request.getColorCode());

            return tagRepository.save(tagModel);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating tag: " + e.getMessage());
        }
    }

    // delete tag
    public void deleteEntry(String id) {
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

    // get all tags
    public List<TagModel> getAllEntries() {
        return tagRepository.findAll();
    }
}
