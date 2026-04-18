package com.hanaro.orgservice.consumer;

import com.hanaro.orgservice.dto.event.OfferingEvent;
import com.hanaro.orgservice.service.OrgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrgConsumer {

    private final OrgService orgService;

    @KafkaListener(topics = "offering-topic", groupId = "org-service-group")
    public void handleOfferingEvent(OfferingEvent event) {
        log.info("Received offering event for orgId: {}, amount: {}", event.getOrgId(), event.getAmount());
        try {
            orgService.updateOfferingAmount(event.getOrgId(), event.getAmount());
        } catch (Exception e) {
            log.error("Failed to update offering amount for orgId: {}", event.getOrgId(), e);
        }
    }
}
