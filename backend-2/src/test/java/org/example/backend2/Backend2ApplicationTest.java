package org.example.backend2;

import org.example.backend2.controller.AiController;
import org.example.backend2.controller.AnalyticsController;
import org.example.backend2.controller.NotificationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class Backend2ApplicationTest {

    @Autowired
    private AiController aiController;

    @Autowired
    private AnalyticsController analyticsController;

    @Autowired
    private NotificationController notificationController;

    @Test
    void contextLoads() {
        assertNotNull(aiController);
        assertNotNull(analyticsController);
        assertNotNull(notificationController);
    }
}
