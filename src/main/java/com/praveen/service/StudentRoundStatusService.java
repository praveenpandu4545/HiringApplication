package com.praveen.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.praveen.dto.BulkUpdateResponse;
import com.praveen.dto.UpdateStudentRoundStatusRequest;

public interface StudentRoundStatusService {
	void updateStatus(Long id, UpdateStudentRoundStatusRequest request);
	
	BulkUpdateResponse bulkUpdateStatus(
            MultipartFile file,
            Long driveId) throws Exception;
	
}
