package com.praveen.dto;

import java.util.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
	private String studentId;
	private String password;
}
