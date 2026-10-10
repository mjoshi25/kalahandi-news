package in.asmj.kalahandiconnect.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class CloudinaryImageService {
    private final Cloudinary cloudinary;
    public CloudinaryImageService(@Value("${cloudinary.cloud-name:${CLOUDINARY_CLOUD_NAME:}}") String cloudName,
                                  @Value("${cloudinary.api-key:${CLOUDINARY_API_KEY:}}") String apiKey,
                                  @Value("${cloudinary.api-secret:${CLOUDINARY_API_SECRET:}}") String apiSecret) {
        this.cloudinary = (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) ? null :
                new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret, "secure", true));
    }
    public String upload(MultipartFile file) throws Exception {
        if (cloudinary == null) throw new IllegalStateException("Cloudinary is not configured. Set CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY and CLOUDINARY_API_SECRET.");
        if (file == null || file.isEmpty()) return null;
        String type = file.getContentType();
        if (type == null || !type.matches("image/(jpeg|png|webp)")) throw new IllegalArgumentException("Only JPG, PNG, and WEBP images are allowed.");
        if (file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("Each image must be 5 MB or smaller.");
        Map<?,?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "kalahandi-connect/news", "resource_type", "image"));
        return String.valueOf(result.get("secure_url"));
    }
}
