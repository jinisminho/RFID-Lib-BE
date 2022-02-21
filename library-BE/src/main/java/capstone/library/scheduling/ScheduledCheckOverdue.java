package capstone.library.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import capstone.library.services.MailService;

@Component("scheduledCheckOverdue")
public class ScheduledCheckOverdue {
    @Autowired
    private MailService mailService;

    @Scheduled(cron = "${cron.expression}")
    public void scheduleCheckOverdue() {
        mailService.sendRemindOverdueBook();
    }

    @Scheduled(cron = "${cron.expression}")
    public void scheduleNotifyWishlist() {
        mailService.sendNotifyWishlistAvailable();
    }
}
