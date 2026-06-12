package com.smartlearning.assistant.knowledge;

import com.smartlearning.assistant.auth.UserPrincipal;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final S3Service s3Service;
    private final KnowledgeTaskRepository taskRepository;
    private final VectorizationService vectorizationService;

    public KnowledgeController(
            S3Service s3Service,
            KnowledgeTaskRepository taskRepository,
            VectorizationService vectorizationService) {
        this.s3Service = s3Service;
        this.taskRepository = taskRepository;
        this.vectorizationService = vectorizationService;
    }

    @PostMapping("/upload-url")
    public ResponseEntity<Map<String, String>> getUploadUrl(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String filename) {
        String objectKey = "uploads/" + user.userId() + "/" + System.currentTimeMillis() + "_" + filename;
        String presignedUrl = s3Service.generatePresignedUrl(objectKey);

        return ResponseEntity.ok(Map.of(
                "uploadUrl", presignedUrl,
                "objectKey", objectKey));
    }

    @PostMapping("/tasks")
    public ResponseEntity<KnowledgeTask> createTask(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String filename,
            @RequestParam String s3Url) {
        KnowledgeTask task = new KnowledgeTask(user.userId(), filename, s3Url);
        task = taskRepository.save(task);

        vectorizationService.processTask(task.getId());

        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<KnowledgeTask>> listTasks(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(
                taskRepository.findByUserIdOrderByCreatedAtDesc(user.userId()));
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<KnowledgeTask> getTask(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long taskId) {
        return taskRepository.findById(taskId)
                .filter(t -> t.getUserId().equals(user.userId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
