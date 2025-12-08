package com.jdk.encher.scheduler;

import com.jdk.encher.entity.Enchere;
import com.jdk.encher.entity.StatutEnchere;
import com.jdk.encher.repository.EnchereRepository;
import com.jdk.encher.service.EnchereService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final EnchereRepository enchereRepository;
    private final EnchereService enchereService;

    /**
     * Check every minute for expired auctions and close them.
     * This ensures credits are transferred and status is updated
     * even if no one visits the auction page.
     */
    @Scheduled(fixedRate = 10000) // Run every 60 seconds
    @Transactional
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Fetch all active auctions that have passed their end date
        List<Enchere> expiredAuctions = enchereRepository.findAll().stream()
                .filter(e -> e.getStatut() == StatutEnchere.EN_COURS && e.getDateFin().isBefore(now))
                .toList();

        if (!expiredAuctions.isEmpty()) {
            System.out.println("Scheduler: Closing " + expiredAuctions.size() + " expired auctions...");
            for (Enchere e : expiredAuctions) {
                try {
                    enchereService.checkAndCloseEnchere(e.getId());
                } catch (Exception ex) {
                    System.err.println("Error closing auction " + e.getId() + ": " + ex.getMessage());
                }
            }
        }
    }
}
