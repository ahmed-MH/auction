package com.jdk.encher.controller;

import com.jdk.encher.entity.Message;
import com.jdk.encher.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MessageController {

    private final MessageRepository messageRepository;

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        // Idéalement à sécuriser pour ADMIN seulement
        return ResponseEntity.ok(messageRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        message.setDateEnvoi(new Date());
        return ResponseEntity.ok(messageRepository.save(message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
