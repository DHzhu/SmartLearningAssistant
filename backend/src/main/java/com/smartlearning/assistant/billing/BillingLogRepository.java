package com.smartlearning.assistant.billing;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingLogRepository extends JpaRepository<BillingLog, Long> {

    List<BillingLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
