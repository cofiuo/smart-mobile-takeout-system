package com.go.entity.query;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class OrderQuery {
    //按照订单的编号查询  orderId就是查询的条件
    @ToolParam(required = false,description = "订单编号")
    private Long orderId;
}
