package in.asmj.kalahandiconnect.repository;

import in.asmj.kalahandiconnect.model.NewsPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface NewsPostRepository extends MongoRepository<NewsPost,String> {
    List<NewsPost> findByStatusAndMobileVerifiedTrueAndEmailVerifiedTrueOrderByCreatedAtDesc(String status);
    List<NewsPost> findAllByOrderByCreatedAtDesc();
    List<NewsPost> findByStatusOrderByCreatedAtDesc(String status);
    Optional<NewsPost> findByReferenceNumber(String referenceNumber);
}
