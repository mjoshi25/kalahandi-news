package in.asmj.kalahandiconnect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("news_posts")
public class NewsPost {
    @Id private String id;
    private String referenceNumber;
    private String title;
    private String category;
    private String location;
    private String description;
    private String mobileNumber;
    private String email;
    private List<String> imageUrls = new ArrayList<>();
    private String mobileVerificationCode;
    private String emailVerificationCode;
    private boolean mobileVerified;
    private boolean emailVerified;
    private String mobileVerifiedBy;
    private Instant mobileVerifiedAt;
    private String emailVerifiedBy;
    private Instant emailVerifiedAt;
    private String status = "PENDING_APPROVAL";
    private String approvedBy;
    private Instant approvedAt;
    private String rejectedBy;
    private Instant rejectedAt;
    private String adminNotes;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public String getId(){return id;} public void setId(String v){id=v;}
    public String getReferenceNumber(){return referenceNumber;} public void setReferenceNumber(String v){referenceNumber=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getMobileNumber(){return mobileNumber;} public void setMobileNumber(String v){mobileNumber=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public List<String> getImageUrls(){return imageUrls;} public void setImageUrls(List<String> v){imageUrls=v==null?new ArrayList<>():v;}
    public String getMobileVerificationCode(){return mobileVerificationCode;} public void setMobileVerificationCode(String v){mobileVerificationCode=v;}
    public String getEmailVerificationCode(){return emailVerificationCode;} public void setEmailVerificationCode(String v){emailVerificationCode=v;}
    public boolean isMobileVerified(){return mobileVerified;} public void setMobileVerified(boolean v){mobileVerified=v;}
    public boolean isEmailVerified(){return emailVerified;} public void setEmailVerified(boolean v){emailVerified=v;}
    public String getMobileVerifiedBy(){return mobileVerifiedBy;} public void setMobileVerifiedBy(String v){mobileVerifiedBy=v;}
    public Instant getMobileVerifiedAt(){return mobileVerifiedAt;} public void setMobileVerifiedAt(Instant v){mobileVerifiedAt=v;}
    public String getEmailVerifiedBy(){return emailVerifiedBy;} public void setEmailVerifiedBy(String v){emailVerifiedBy=v;}
    public Instant getEmailVerifiedAt(){return emailVerifiedAt;} public void setEmailVerifiedAt(Instant v){emailVerifiedAt=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getApprovedBy(){return approvedBy;} public void setApprovedBy(String v){approvedBy=v;}
    public Instant getApprovedAt(){return approvedAt;} public void setApprovedAt(Instant v){approvedAt=v;}
    public String getRejectedBy(){return rejectedBy;} public void setRejectedBy(String v){rejectedBy=v;}
    public Instant getRejectedAt(){return rejectedAt;} public void setRejectedAt(Instant v){rejectedAt=v;}
    public String getAdminNotes(){return adminNotes;} public void setAdminNotes(String v){adminNotes=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
