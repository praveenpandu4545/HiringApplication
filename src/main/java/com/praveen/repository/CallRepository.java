package com.praveen.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.Call;

public interface CallRepository extends JpaRepository<Call, Long> {

    Call findTopByReceiverIdAndStatusInOrderByIdDesc(
        Long receiverId,
        List<String> statuses
    );
}