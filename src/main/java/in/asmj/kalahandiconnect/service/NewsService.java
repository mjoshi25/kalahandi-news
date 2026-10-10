package in.asmj.kalahandiconnect.service;

import in.asmj.kalahandiconnect.model.NewsPost;
import in.asmj.kalahandiconnect.model.NewsSubmissionForm;
import in.asmj.kalahandiconnect.repository.NewsPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class NewsService {
    private final NewsPostRepository repository;
    private final CloudinaryImageService images;
    private final SecureRandom random = new SecureRandom();
    public NewsService(NewsPostRepository repository, CloudinaryImageService images) { this.repository=repository; this.images=images; }

    public NewsPost submit(NewsSubmissionForm form) throws Exception {
        if (form.getImages() == null || form.getImages().stream().filter(f -> f != null && !f.isEmpty()).count() > 5)
            throw new IllegalArgumentException("Upload no more than 5 images.");
        NewsPost post = new NewsPost();
        post.setReferenceNumber("KAL-" + Instant.now().toEpochMilli() + "-" + (100 + random.nextInt(900)));
        post.setTitle(form.getTitle().trim()); post.setCategory(form.getCategory().trim());
        post.setLocation(form.getLocation().trim()); post.setDescription(form.getDescription().trim());
        post.setMobileNumber(form.getMobileNumber().trim()); post.setEmail(form.getEmail().trim().toLowerCase(Locale.ROOT));
        post.setMobileVerificationCode("KAL-M-" + (1000 + random.nextInt(9000)));
        post.setEmailVerificationCode("KAL-E-" + (1000 + random.nextInt(9000)));
        post.setMobileVerified(false); post.setEmailVerified(false); post.setStatus("PENDING_APPROVAL");
        List<String> urls = new ArrayList<>();
        if (form.getImages() != null) for (MultipartFile file : form.getImages()) {
            if (file != null && !file.isEmpty()) { String url = images.upload(file); if (url != null) urls.add(url); }
        }
        post.setImageUrls(urls); post.setCreatedAt(Instant.now()); post.setUpdatedAt(Instant.now());
        return repository.save(post);
    }

    public List<NewsPost> publicNews() { return repository.findByStatusAndMobileVerifiedTrueAndEmailVerifiedTrueOrderByCreatedAtDesc("PUBLISHED"); }
    public List<NewsPost> allNews() { return repository.findAllByOrderByCreatedAtDesc(); }
    public List<NewsPost> pendingNews() { return repository.findByStatusOrderByCreatedAtDesc("PENDING_APPROVAL"); }
    public NewsPost find(String id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("News submission not found.")); }
    public NewsPost verifyMobile(String id, boolean verified, String admin) {
        NewsPost p=find(id); p.setMobileVerified(verified); p.setMobileVerifiedBy(admin); p.setMobileVerifiedAt(Instant.now()); p.setUpdatedAt(Instant.now());
        if (!verified && "PUBLISHED".equals(p.getStatus())) unpublish(p, admin, "Mobile verification revoked");
        return repository.save(p);
    }
    public NewsPost verifyEmail(String id, boolean verified, String admin) {
        NewsPost p=find(id); p.setEmailVerified(verified); p.setEmailVerifiedBy(admin); p.setEmailVerifiedAt(Instant.now()); p.setUpdatedAt(Instant.now());
        if (!verified && "PUBLISHED".equals(p.getStatus())) unpublish(p, admin, "Email verification revoked");
        return repository.save(p);
    }
    public NewsPost approve(String id, String admin, String notes) {
        NewsPost p=find(id);
        if (!p.isMobileVerified() || !p.isEmailVerified()) throw new IllegalStateException("Verify both mobile number and email before approving this news.");
        p.setStatus("PUBLISHED"); p.setApprovedBy(admin); p.setApprovedAt(Instant.now()); p.setRejectedBy(null); p.setRejectedAt(null); p.setAdminNotes(notes); p.setUpdatedAt(Instant.now());
        return repository.save(p);
    }
    public NewsPost reject(String id, String admin, String notes) {
        NewsPost p=find(id); p.setStatus("REJECTED"); p.setRejectedBy(admin); p.setRejectedAt(Instant.now()); p.setApprovedBy(null); p.setApprovedAt(null); p.setAdminNotes(notes); p.setUpdatedAt(Instant.now()); return repository.save(p);
    }
    public NewsPost unpublish(String id, String admin, String notes) { NewsPost p=find(id); unpublish(p,admin,notes); return repository.save(p); }
    private void unpublish(NewsPost p, String admin, String notes) { p.setStatus("PENDING_APPROVAL"); p.setApprovedBy(null); p.setApprovedAt(null); p.setAdminNotes(notes + " (action by " + admin + ")"); p.setUpdatedAt(Instant.now()); }
}
