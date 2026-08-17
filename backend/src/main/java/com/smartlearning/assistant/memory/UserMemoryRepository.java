package com.smartlearning.assistant.memory;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMemoryRepository extends JpaRepository<UserMemory, Long> {

    List<UserMemory> findByUserIdOrderByImportanceDescUpdatedAtDesc(Long userId);

    List<UserMemory> findByUserIdAndCategory(Long userId, String category);

    void deleteByIdAndUserId(Long id, Long userId);
}
