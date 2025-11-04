package com.jdk.encher.controller;

import com.jdk.encher.entity.Image;
import com.jdk.encher.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/encher/{encherId}")
    public ResponseEntity<List<Image>> getImagesByEncher(@PathVariable Long encherId) {
        List<Image> images = imageService.getImagesByEncher(encherId);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/encher/{encherId}")
    public ResponseEntity<Image> addImageToEncher(@PathVariable Long encherId, @RequestBody Image image) {
        Image savedImage = imageService.addImageToEncher(encherId, image);
        return ResponseEntity.ok(savedImage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}