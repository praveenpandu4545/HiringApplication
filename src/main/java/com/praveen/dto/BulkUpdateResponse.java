package com.praveen.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateResponse {

    private int totalRows;
    private int successCount;
    private int failedCount;
    private List<String> failedMessages;


}