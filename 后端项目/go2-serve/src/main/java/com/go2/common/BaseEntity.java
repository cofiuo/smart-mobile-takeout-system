package com.go2.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor     //全参构造
@NoArgsConstructor      //无参构造
public class BaseEntity {
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer statu;
}
