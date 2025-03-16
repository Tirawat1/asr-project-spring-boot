package cs.project.TextToSpeech.services;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONObject;

@Service
public class AsrService {
    private final RestTemplate restTemplate;

    public AsrService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendAudioToEnhanceService(String audioUrl) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("signed_url", audioUrl); 

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        // Make the POST request to the Flask service
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(
            "http://192.168.1.35:5114/enhance_audio", 
            requestEntity, 
            String.class
        );

        return response;
    } catch (Exception e) {
        throw new RuntimeException("Error enhancing audio: " + e.getMessage(), e);
    }
}
}
