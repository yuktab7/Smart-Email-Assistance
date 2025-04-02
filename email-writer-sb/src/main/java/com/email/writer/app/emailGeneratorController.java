package com.email.writer.app;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class emailGeneratorController {
    private final EmailGeneraterService emailGeneraterService;

    public emailGeneratorController(EmailGeneraterService emailGeneraterService) {
        this.emailGeneraterService = emailGeneraterService;
    }


    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest)
    {
        String response=emailGeneraterService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}
