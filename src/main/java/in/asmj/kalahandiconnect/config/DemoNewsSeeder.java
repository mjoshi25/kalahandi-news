package in.asmj.kalahandiconnect.config;

import in.asmj.kalahandiconnect.model.NewsPost;
import in.asmj.kalahandiconnect.repository.NewsPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Inserts any missing, clearly labelled demo articles without overwriting existing news. */
@Configuration
public class DemoNewsSeeder {
    private static final Logger log = LoggerFactory.getLogger(DemoNewsSeeder.class);
    @Bean
    ApplicationRunner seedDemoNews(NewsPostRepository repository) {
        return args -> {
            Instant now = Instant.now();
            List<NewsPost> samples = List.of(
                sample("KAL-DEMO-001", "[DEMO] Farmers' field clinic highlights soil health and crop planning", "Agriculture", "Bhawanipatna",
                    "DEMONSTRATION CONTENT — Illustrative agriculture article. A real report could cover a field clinic about soil testing, crop planning and seed selection. Before publication, replace this with verified event details, quotes and information from the responsible department.", now.minus(1, ChronoUnit.DAYS)),
                sample("KAL-DEMO-002", "[DEMO] Local artisans prepare for a district handicrafts showcase", "Culture", "Bhawanipatna",
                    "DEMONSTRATION CONTENT — Illustrative culture and livelihoods article. A real report could introduce local craftspeople and traditional arts. Confirm names, dates, venue and statements with organisers before using this as real news.", now.minus(2, ChronoUnit.DAYS)),
                sample("KAL-DEMO-003", "[DEMO] School community plans a reading and library improvement drive", "Education", "Kesinga",
                    "DEMONSTRATION CONTENT — Placeholder editorial content, not an announcement of an actual school event. Replace it with a verified report including the institution name, date, activities and comments from authorised representatives.", now.minus(3, ChronoUnit.DAYS)),
                sample("KAL-DEMO-004", "[DEMO] Residents discuss drinking-water service and maintenance", "Civic Issues", "Thuamul Rampur",
                    "DEMONSTRATION CONTENT — Sample public-service report. For a real article, confirm the affected locality, duration of any disruption, response from the relevant department and next steps communicated to residents. Do not publish unverified allegations or service updates.", now.minus(4, ChronoUnit.DAYS)),
                sample("KAL-DEMO-005", "[DEMO] Youth volunteers plan a neighbourhood cleanliness campaign", "Community", "Junagarh",
                    "DEMONSTRATION CONTENT — Placeholder community-news article. Replace it with confirmed details from organisers, including the actual date, location, participating groups and public instructions. This sample does not announce a real campaign.", now.minus(5, ChronoUnit.DAYS)),
                sample("KAL-DEMO-006", "[DEMO] Small-business profile: local markets and home-grown enterprise", "Business", "Dharamgarh",
                    "DEMONSTRATION CONTENT — Sample local-business feature. A publishable version should identify the business with permission, verify the owner's comments and describe products or services accurately without unsupported claims.", now.minus(6, ChronoUnit.DAYS))
            );

            List<NewsPost> missing = new ArrayList<>();
            for (NewsPost sample : samples) {
                if (repository.findByReferenceNumber(sample.getReferenceNumber()).isEmpty()) {
                    missing.add(sample);
                }
            }
            if (!missing.isEmpty()) {
                repository.saveAll(missing);
                log.info("Inserted {} clearly labelled demo news stories into MongoDB.", missing.size());
            } else {
                log.info("All {} demo news stories already exist in MongoDB; no seed records inserted.", samples.size());
            }
            long publishedDemoCount = repository.findByStatusAndMobileVerifiedTrueAndEmailVerifiedTrueOrderByCreatedAtDesc("PUBLISHED")
                .stream().filter(p -> p.getReferenceNumber() != null && p.getReferenceNumber().startsWith("KAL-DEMO-")).count();
            log.info("Verified/published demo stories visible to the homepage query: {}", publishedDemoCount);
        };
    }

    private NewsPost sample(String reference, String title, String category, String location, String description, Instant createdAt) {
        NewsPost post = new NewsPost();
        post.setReferenceNumber(reference);
        post.setTitle(title);
        post.setCategory(category);
        post.setLocation(location);
        post.setDescription(description);
        post.setMobileNumber("0000000000");
        post.setEmail("demo@example.invalid");
        post.setImageUrls(List.of());
        post.setMobileVerificationCode("DEMO-NOT-A-REAL-CODE");
        post.setEmailVerificationCode("DEMO-NOT-A-REAL-CODE");
        post.setMobileVerified(true);
        post.setEmailVerified(true);
        post.setMobileVerifiedBy("demo-seeder");
        post.setEmailVerifiedBy("demo-seeder");
        post.setMobileVerifiedAt(createdAt);
        post.setEmailVerifiedAt(createdAt);
        post.setStatus("PUBLISHED");
        post.setApprovedBy("demo-seeder");
        post.setApprovedAt(createdAt);
        post.setAdminNotes("DEMO DATA ONLY — replace with verified reporting before production.");
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(createdAt);
        return post;
    }
}
