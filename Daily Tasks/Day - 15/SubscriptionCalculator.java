import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SubscriptionCalculator {

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    // Custom Exception for missing entity lookups
    public static class SubscriptionNotFoundException extends RuntimeException {
        public SubscriptionNotFoundException(String message) {
            super(message);
        }
    }

    public enum PlanTier {
        MONTHLY(Period.ofMonths(1), 3),
        QUARTERLY(Period.ofMonths(3), 5),
        ANNUAL(Period.ofYears(1), 14);

        private final Period duration;
        private final int gracePeriodDays;

        PlanTier(Period duration, int gracePeriodDays) {
            this.duration = duration;
            this.gracePeriodDays = gracePeriodDays;
        }

        public Period getDuration() {
            return duration;
        }

        public int getGracePeriodDays() {
            return gracePeriodDays;
        }
    }

    public record Subscription(
            String id,
            String userId,
            PlanTier planTier,
            ZonedDateTime activatedAtUtc
    ) {}

    public record SubscriptionStatus(
            String subscriptionId,
            ZonedDateTime expiryUtc,
            ZonedDateTime expiryIst,
            ZonedDateTime gracePeriodEndUtc,
            ZonedDateTime gracePeriodEndIst,
            long daysRemaining,
            boolean isGracePeriodActive,
            boolean isExpired
    ) {}

    // Repository refactored to eliminate null returns using Optional
    public static class SubscriptionRepository {
        private final Map<String, Subscription> database = new HashMap<>();

        public void save(Subscription subscription) {
            database.put(subscription.id(), subscription);
        }

        public Optional<Subscription> findById(String id) {
            return Optional.ofNullable(database.get(id));
        }

        public Optional<Subscription> findByUserId(String userId) {
            return database.values().stream()
                    .filter(sub -> sub.userId().equalsIgnoreCase(userId))
                    .findFirst();
        }
    }

    // Lookup service returning models or throwing custom exceptions
    public static class SubscriptionService {
        private final SubscriptionRepository repository;

        public SubscriptionService(SubscriptionRepository repository) {
            this.repository = repository;
        }

        public Subscription getSubscriptionOrThrow(String id) {
            return repository.findById(id)
                    .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found with ID: " + id));
        }

        public Subscription getSubscriptionByUserIdOrThrow(String userId) {
            return repository.findByUserId(userId)
                    .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for User ID: " + userId));
        }

        public SubscriptionStatus calculateStatus(String subscriptionId, ZonedDateTime nowUtc) {
            Subscription sub = getSubscriptionOrThrow(subscriptionId);

            ZonedDateTime activationUtc = sub.activatedAtUtc().withZoneSameInstant(UTC);
            ZonedDateTime expiryUtc = activationUtc.plus(sub.planTier().getDuration());
            ZonedDateTime gracePeriodEndUtc = expiryUtc.plusDays(sub.planTier().getGracePeriodDays());

            // Zone conversions to IST
            ZonedDateTime expiryIst = expiryUtc.withZoneSameInstant(IST);
            ZonedDateTime gracePeriodEndIst = gracePeriodEndUtc.withZoneSameInstant(IST);

            long daysRemaining = Math.max(0, Duration.between(nowUtc, expiryUtc).toDays());
            boolean isGracePeriodActive = nowUtc.isAfter(expiryUtc) && !nowUtc.isAfter(gracePeriodEndUtc);
            boolean isExpired = nowUtc.isAfter(gracePeriodEndUtc);

            return new SubscriptionStatus(
                    sub.id(),
                    expiryUtc,
                    expiryIst,
                    gracePeriodEndUtc,
                    gracePeriodEndIst,
                    daysRemaining,
                    isGracePeriodActive,
                    isExpired
            );
        }
    }

    public static void main(String[] args) {
        SubscriptionRepository repository = new SubscriptionRepository();
        SubscriptionService service = new SubscriptionService(repository);

        // Seed mock subscriptions in UTC
        ZonedDateTime activationTime = ZonedDateTime.of(2026, 9, 1, 10, 0, 0, 0, UTC);

        Subscription activeSub = new Subscription("SUB-101", "USR-A", PlanTier.QUARTERLY, activationTime);
        Subscription expiredSub = new Subscription("SUB-102", "USR-B", PlanTier.MONTHLY, activationTime.minusMonths(2));

        repository.save(activeSub);
        repository.save(expiredSub);

        // Verification Point: October 15, 2026
        ZonedDateTime evaluationInstantUtc = ZonedDateTime.of(2026, 10, 15, 12, 0, 0, 0, UTC);

        System.out.println("==================================================");
        System.out.println("        SUBSCRIPTION & TIMEZONE AUDIT             ");
        System.out.println("==================================================");

        printAudit(service.calculateStatus("SUB-101", evaluationInstantUtc));
        printAudit(service.calculateStatus("SUB-102", evaluationInstantUtc));

        // Exception Handling Demonstration
        System.out.println("--- Testing orElseThrow on Missing Record ---");
        try {
            service.getSubscriptionOrThrow("SUB-999");
        } catch (SubscriptionNotFoundException ex) {
            System.out.println("Caught Expected Exception: " + ex.getMessage());
        }
    }

    private static void printAudit(SubscriptionStatus status) {
        System.out.println("Subscription ID        : " + status.subscriptionId());
        System.out.println("Expiry (UTC)           : " + status.expiryUtc().format(FORMATTER));
        System.out.println("Expiry (IST)           : " + status.expiryIst().format(FORMATTER));
        System.out.println("Grace Period End (UTC) : " + status.gracePeriodEndUtc().format(FORMATTER));
        System.out.println("Grace Period End (IST) : " + status.gracePeriodEndIst().format(FORMATTER));
        System.out.println("Days Remaining         : " + status.daysRemaining());
        System.out.println("In Grace Period?       : " + status.isGracePeriodActive());
        System.out.println("Fully Expired?         : " + status.isExpired());
        System.out.println("--------------------------------------------------");
    }
}