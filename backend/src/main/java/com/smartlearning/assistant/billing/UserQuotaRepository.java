package com.smartlearning.assistant.billing;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuotaRepository extends JpaRepository<UserQuota, Long> {

    Optional<UserQuota> findByUserId(Long userId);
}
