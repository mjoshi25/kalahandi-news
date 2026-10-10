package in.asmj.kalahandiconnect.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

public class NewsSubmissionForm {
    @NotBlank @Size(max=140) private String title;
    @NotBlank private String category;
    @NotBlank @Size(max=120) private String location;
    @NotBlank @Size(min=30,max=10000) private String description;
    @NotBlank @Pattern(regexp="[6-9][0-9]{9}", message="Enter a valid 10-digit Indian mobile number") private String mobileNumber;
    @NotBlank @Email @Size(max=180) private String email;
    private List<MultipartFile> images = new ArrayList<>();
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getMobileNumber(){return mobileNumber;} public void setMobileNumber(String v){mobileNumber=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public List<MultipartFile> getImages(){return images;} public void setImages(List<MultipartFile> v){images=v;}
}
