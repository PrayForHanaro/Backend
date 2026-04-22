package com.hanaro.userservice.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDTO {
  private String name;
  private LocalDate birth;       // yyyy-MM-dd 추천
  private String phone;
  private String password;
}
