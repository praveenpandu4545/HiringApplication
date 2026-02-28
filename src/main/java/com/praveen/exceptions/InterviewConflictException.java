package com.praveen.exceptions;

import com.praveen.dto.InterviewConflictResponse;
import lombok.Getter;

@Getter
public class InterviewConflictException extends RuntimeException {

    private final InterviewConflictResponse conflictResponse;

    public InterviewConflictException(InterviewConflictResponse response) {
        super(response.getMessage());
        this.conflictResponse = response;
    }
}