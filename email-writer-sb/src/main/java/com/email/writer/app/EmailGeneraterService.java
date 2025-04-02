package com.email.writer.app;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

@Service
public class EmailGeneraterService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneraterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public String generateEmailReply(EmailRequest emailRequest)
    {



        //bulid the prompt
        String prompt=buildPrompt(emailRequest);

        //craft a request
        Map<String, Object> requestbody=Map.of(
                "contents",new Object[]
                        {
                        Map.of("parts",new Object[]
                                {
                                        Map.of("text",prompt)
                                })
                        }
        );
        //do request and get response
        String response=webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestbody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //return response

        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try
        {
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        }catch (Exception e)
        {
            return "error processing request" + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {

        StringBuilder prompt=new StringBuilder();
        prompt.append("generate a professional email reply for the following email content.please dont generate a subject line");
        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty())
        {
            prompt.append("use a").append(emailRequest.getTone()).append(" tone ");
        }
        prompt.append("\noriginal email ").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
