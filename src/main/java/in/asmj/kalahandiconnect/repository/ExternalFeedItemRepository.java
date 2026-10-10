package in.asmj.kalahandiconnect.repository;

import in.asmj.kalahandiconnect.model.ExternalFeedItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ExternalFeedItemRepository extends MongoRepository<ExternalFeedItem,String> {
    List<ExternalFeedItem> findByStatusOrderByDiscoveredAtDesc(String status);
    List<ExternalFeedItem> findByStatusInOrderByPublishedAtDesc(List<String> statuses);
    Optional<ExternalFeedItem> findBySourceUrl(String sourceUrl);
}
