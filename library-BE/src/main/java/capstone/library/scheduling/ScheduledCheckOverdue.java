package capstone.library.scheduling;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component("scheduledCheckOverdue")
public class ScheduledCheckOverdue {
    @Scheduled(cron = "${cron.expression}")
    public void scheduleTaskUsingExternalizedCronExpression() {
        System.out.println("schedule tasks using externalized cron expressions - " + System.currentTimeMillis() / 1000);
    }
}
