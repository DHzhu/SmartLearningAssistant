package com.smartlearning.assistant.llmops;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LlmTraceRepository extends JpaRepository<LlmTraceRecord, Long> {

    List<LlmTraceRecord> findTop50ByOrderByCreatedAtDesc();

    List<LlmTraceRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(String status);
}
