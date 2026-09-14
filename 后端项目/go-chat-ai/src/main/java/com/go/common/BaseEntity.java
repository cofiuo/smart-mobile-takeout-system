package com.go.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
//所有实体类的父类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    private LocalDateTime created;

    private LocalDateTime updated;

    private Integer statu;   //1正常   0禁用
}
