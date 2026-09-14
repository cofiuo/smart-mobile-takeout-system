package com.go.tool;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.go.entity.Business;
import com.go.entity.Orders;
import com.go.entity.Ordersdetailet;
import com.go.entity.query.OrderQuery;
import com.go.service.BusinessService;
import com.go.service.OrdersService;
import com.go.service.OrdersdetailetService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderTools {
    private final OrdersService ordersService;
    private final OrdersdetailetService odService;
    private final BusinessService businessService;

    @Tool(name="queryOrders",description = "根据订单编号查询订单详细信息,入参仅支持Long类型的订单编号，无其他参数")
    public List<Orders> queryOrders(@ToolParam(required = false,description = "查询订单条件")OrderQuery query){
        QueryChainWrapper<Orders> wrapper = ordersService.query();
        wrapper.eq(query.getOrderId()!=null,"order_id",query.getOrderId());

        List<Orders> ordersList = wrapper.list();
        ordersList.forEach(orders -> {
            List<Ordersdetailet> odList = odService.listAll(orders.getOrderId());
            orders.setOdList(odList);

            Business business = businessService.getById(orders.getBusinessId());
            orders.setBusiness(business);
        });
        return ordersList;
    }
}
