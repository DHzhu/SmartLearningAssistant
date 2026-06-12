package com.smartlearning.assistant.knowledge;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeTaskRepository extends JpaRepository<KnowledgeTask, Long> {

    List<KnowledgeTask> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<KnowledgeTask> findByStatus(KnowledgeTask.Status status);
}
