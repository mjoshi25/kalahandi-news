package in.asmj.kalahandiconnect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("external_feed_items")
public class ExternalFeedItem {
    @Id private String id;
    private String sourceTitle;
    private String sourceUrl;
    private String sourceName;
    private String title;
    private String summary;
    private String category = "District Updates";
    private String status = "PENDING_REVIEW";
    private Instant publishedAt;
    private Instant discoveredAt = Instant.now();
    private Instant reviewedAt;
    private String reviewedBy;
    private String adminNotes;
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getSourceTitle(){return sourceTitle;} public void setSourceTitle(String v){sourceTitle=v;}
    public String getSourceUrl(){return sourceUrl;} public void setSourceUrl(String v){sourceUrl=v;}
    public String getSourceName(){return sourceName;} public void setSourceName(String v){sourceName=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Instant getPublishedAt(){return publishedAt;} public void setPublishedAt(Instant v){publishedAt=v;}
    public Instant getDiscoveredAt(){return discoveredAt;} public void setDiscoveredAt(Instant v){discoveredAt=v;}
    public Instant getReviewedAt(){return reviewedAt;} public void setReviewedAt(Instant v){reviewedAt=v;}
    public String getReviewedBy(){return reviewedBy;} public void setReviewedBy(String v){reviewedBy=v;}
    public String getAdminNotes(){return adminNotes;} public void setAdminNotes(String v){adminNotes=v;}
}
