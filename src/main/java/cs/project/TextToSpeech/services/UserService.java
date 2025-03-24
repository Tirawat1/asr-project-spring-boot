package cs.project.TextToSpeech.services;

import java.util.*;

import cs.project.TextToSpeech.infra.repository.*;
import cs.project.TextToSpeech.models.DTO.auth.RegisterUserDTO;
import cs.project.TextToSpeech.models.DTO.user.UpdateUserDTO;
import cs.project.TextToSpeech.models.DTO.user.UserWithImageUrl;
import cs.project.TextToSpeech.models.Request.DiaryFolderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cs.project.TextToSpeech.models.UserModel;

import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private DiaryFolderService diaryFolderService;
    @Autowired
    private MinioService minioService;


    public UserService(UserRepository userRepository, MinioService minioService) {
        this.userRepository = userRepository;
        this.minioService = minioService;
    }

    // Get all users
    public List<UserModel> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all users: " + e.getMessage());
        }
    }

    // Get a user by ID
    public UserModel getUserById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }
            return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: " + id));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    public UserModel getUserByEmail(String email) {
        try {
            UserModel user = userRepository.findByEmail(email).orElse(null);
            Objects.requireNonNull(user, "User not found");
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error getting user by email: " + e.getMessage());
        }
    }

    public UserModel createUser(RegisterUserDTO registerUserDTO) {
        try {
            // check exist
            if (userRepository.findByEmail(registerUserDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Create user
            UserModel user = new UserModel();
            user.setName(registerUserDTO.getName());
            user.setEmail(registerUserDTO.getEmail());

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(registerUserDTO.getPassword()));
            user = userRepository.save(user);


            DiaryFolderRequest diaryFolderRequest = new DiaryFolderRequest();
            diaryFolderRequest.setFolderName("Default");
            diaryFolderRequest.setDiaryIds(new ArrayList<>());

            diaryFolderService.createPersonalDiaryFolder(user.getId(), diaryFolderRequest);
            return user;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage());
        }
    }

    public UserWithImageUrl putUser(String id, UpdateUserDTO updateUserDTO) {
        try {
            UserModel user = userRepository.findById(id).orElse(null);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            if (updateUserDTO.getName() != null && !updateUserDTO.getName().trim().isEmpty()) {
                user.setName(updateUserDTO.getName());
            }

            System.out.println("test: " + updateUserDTO.getProfileImgPath());
            if (updateUserDTO.getProfileImgPath() != null) {
                String path = minioService.uploadImageFile(updateUserDTO.getProfileImgPath(), user.getId());
                user.setProfileImgPath(path);
            }

            user = userRepository.save(user);

            return getUserWithImageUrl(user.getId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Delete a user by ID
    public void deleteUser(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be empty");
            }

            UserModel user = userRepository.findById(id).orElse(null);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + id);
            }

            // Delete all diary folders and diaries of user
            diaryFolderService.deletePersonalFolder(id);


            userRepository.deleteById(id);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    public UserWithImageUrl getUserWithImageUrl(String id) {
        try {
            UserModel user = userRepository.findById(id).orElse(null);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + id);
            }

            String imageUrl;
            if (user.getProfileImgPath() != null) {
                try {
                    imageUrl = minioService.getUserProfilePresignedObjectUrl(user.getProfileImgPath());
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile image not found: " + e.getMessage());
                }
            } else {
                imageUrl = null;
            }

            System.out.println("imageUrl: " + imageUrl);

            return new UserWithImageUrl(user, imageUrl);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    public DiaryFolderModel addUserDiaryFolder(String userId, DiaryFolderRequest diaryFolderRequest) {
//        if (userId == null || userId.trim().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID cannot be empty");
//        }
//
//        UserModel user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
//
//        DiaryFolderModel diaryFolderModel = diaryFolderService.createFolder(diaryFolderRequest);
//
//        user.getDiaryFolderIds().add(diaryFolderModel.getDiaryFolderId());
//        userRepository.save(user);
//
//        return diaryFolderModel;
//    }
//
//    public boolean removeUserDiaryFolder(String userId, String diaryFolderId) {
//        Objects.requireNonNull(userId, "User ID cannot be null");
//        Objects.requireNonNull(diaryFolderId, "Diary folder ID cannot be null");
//
//        UserModel user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found with ID: " + userId));
//
//        boolean removed = user.getDiaryFolderIds().removeIf(folderId -> folderId.equals(diaryFolderId));
//        userRepository.save(user);
//
//        if (!removed) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary folder not found or does not belong to user: " + diaryFolderId);
//        }
//
//        if (!diaryFolderRepository.existsById(diaryFolderId)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary folder does not exist with ID: " + diaryFolderId);
//        }
//
//        try {
//            return diaryFolderService.deleteFolder(diaryFolderId);
//        } catch (EmptyResultDataAccessException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete diary folder: " + diaryFolderId);
//        }
//    }
}
