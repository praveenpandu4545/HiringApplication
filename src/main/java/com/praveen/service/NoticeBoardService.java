package com.praveen.service;

import java.util.List;

import com.praveen.dto.NoticeBoardDTO;
import com.praveen.entities.NoticeBoard;

public interface NoticeBoardService {
	String createNotice(NoticeBoardDTO dto);
	List<NoticeBoard> getAllNotices();
}
