package com.my.shop.utils;

import com.my.shop.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderCleanupScheduler {
    private final OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void deleteUnpaidOrdersScheduled() {
        orderService.deleteUnpaidOrders();
    }
}
