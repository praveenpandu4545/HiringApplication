package com.praveen.service;

import com.praveen.dto.NoticeBoardDTO;
import com.praveen.entities.NoticeAttachment;
import com.praveen.entities.NoticeBoard;
import com.praveen.repository.NoticeBoardRepository;
import com.praveen.service.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeBoardImpl implements NoticeBoardService {

    private final NoticeBoardRepository noticeBoardRepository;

    @Override
    public String createNotice(NoticeBoardDTO dto) {

        try {

            NoticeBoard notice = NoticeBoard.builder()
                    .heading(dto.getHeading())
                    .body(dto.getBody())
                    .build();

            List<NoticeAttachment> attachmentList =
                    new ArrayList<>();

            if (dto.getAttachments() != null) {

                for (MultipartFile file : dto.getAttachments()) {

                    if (!file.isEmpty()) {

                        NoticeAttachment attachment =
                                NoticeAttachment.builder()
                                        .fileName(
                                                file.getOriginalFilename()
                                        )
                                        .contentType(
                                                file.getContentType()
                                        )
                                        .data(file.getBytes())
                                        .noticeBoard(notice)
                                        .build();

                        attachmentList.add(attachment);
                    }
                }
            }

            notice.setAttachments(attachmentList);

            noticeBoardRepository.save(notice);

            return "Notice created successfully";

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload attachments",
                    e
            );
        }
    }

    @Override
    public List<NoticeBoard> getAllNotices() {
        return noticeBoardRepository.findAll();
    }
}