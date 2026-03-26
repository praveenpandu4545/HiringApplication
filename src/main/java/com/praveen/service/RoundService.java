package com.praveen.service;

import java.util.List;
import com.praveen.dto.RoundResponse;

public interface RoundService {
	List<RoundResponse> getRoundsById(Long id);
}
