
package com.praveen.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRoundStatusRequest {
    private String status; // PENDING / SELECTED / REJECTED
}
