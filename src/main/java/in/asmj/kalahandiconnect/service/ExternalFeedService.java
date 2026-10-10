package in.asmj.kalahandiconnect.service;

import in.asmj.kalahandiconnect.model.ExternalFeedItem;
import in.asmj.kalahandiconnect.repository.ExternalFeedItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class ExternalFeedService {
    private static final Logger log = LoggerFactory.getLogger(ExternalFeedService.class);
    private final ExternalFeedItemRepository repository;
    private final RestClient client = RestClient.builder().build();
    private final String feedUrls;
    private final String openAiKey;

    public ExternalFeedService(ExternalFeedItemRepository repository,
            @Value("${app.live-feed.rss-urls:}") String feedUrls,
            @Value("${OPENAI_API_KEY:}") String openAiKey) {
        this.repository=repository; this.feedUrls=feedUrls; this.openAiKey=openAiKey;
    }

    @Scheduled(fixedDelayString = "${app.live-feed.refresh-ms:900000}", initialDelayString = "${app.live-feed.initial-delay-ms:15000}")
    public void refreshFeeds() {
        if (feedUrls.isBlank()) { log.info("Live Feed: no RSS sources configured; set LIVE_FEED_RSS_URLS to enable discovery."); return; }
        for (String raw : feedUrls.split(",")) {
            String url=raw.trim(); if (url.isBlank()) continue;
            try { ingest(url); } catch (Exception ex) { log.warn("Could not refresh configured live-feed source {}: {}", safeHost(url), ex.getMessage()); }
        }
    }

    private void ingest(String feedUrl) throws Exception {
        String xml=client.get().uri(URI.create(feedUrl)).retrieve().body(String.class);
        if (xml==null || xml.isBlank()) return;
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false); factory.setExpandEntityReferences(false);
        Document doc=factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        NodeList entries=doc.getElementsByTagName("item");
        if (entries.getLength()==0) entries=doc.getElementsByTagNameNS("*","entry");
        String source=doc.getElementsByTagName("title").getLength()>0 ? doc.getElementsByTagName("title").item(0).getTextContent() : safeHost(feedUrl);
        for(int i=0;i<Math.min(entries.getLength(),50);i++) {
            Element entry=(Element)entries.item(i);
            String title=child(entry,"title");
            String link=child(entry,"link");
            if (link.isBlank()) {
                NodeList links=entry.getElementsByTagNameNS("*","link");
                if(links.getLength()>0) { Element l=(Element)links.item(0); link=l.hasAttribute("href")?l.getAttribute("href"):l.getTextContent(); }
            }
            String description=child(entry,"description");
            if(description.isBlank()) description=child(entry,"summary");
            if(description.isBlank()) description=child(entry,"content:encoded");
            if(title.isBlank() || link.isBlank() || !link.matches("^https?://.*")) continue;
            if(repository.findBySourceUrl(link).isPresent()) continue;
            ExternalFeedItem item=new ExternalFeedItem();
            item.setTitle(title.trim()); item.setSourceTitle(title.trim()); item.setSourceUrl(link.trim());
            item.setSourceName(source); item.setSummary(summarize(description, title));
            item.setCategory(categorize(title+" "+description)); item.setStatus("PENDING_REVIEW");
            item.setPublishedAt(Instant.now()); item.setDiscoveredAt(Instant.now());
            repository.save(item);
        }
    }

    private String summarize(String description, String title) {
        String plain=description.replaceAll("(?is)<script.*?</script>|<style.*?</style>", " ")
                .replaceAll("<[^>]+>", " ").replaceAll("&nbsp;"," ").replaceAll("&amp;","&")
                .replaceAll("\\s+"," ").trim();
        if(plain.length()>700) plain=plain.substring(0,697)+"...";
        if(!openAiKey.isBlank() && !plain.isBlank()) {
            try {
                Map<?,?> result=client.post().uri("https://api.openai.com/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+openAiKey)
                    .body(Map.of("model","gpt-4o-mini","temperature",0.2,"messages",List.of(
                        Map.of("role","system","content","Summarize the provided public news item in 2 concise sentences. Do not add facts. If details are unclear, say the source has limited detail. Output only the summary."),
                        Map.of("role","user","content","Headline: "+title+"\nSource text: "+plain))))
                    .retrieve().body(Map.class);
                if(result!=null) {
                    Object choices=result.get("choices");
                    if(choices instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?,?> c && c.get("message") instanceof Map<?,?> m) {
                        Object content=m.get("content"); if(content instanceof String s && !s.isBlank()) return s.trim();
                    }
                }
            } catch(Exception e) { log.debug("AI summary unavailable; using source excerpt: {}",e.getMessage()); }
        }
        return plain.isBlank() ? "Open the original source for the full report. This external item has not yet been independently verified." : plain;
    }
    private String categorize(String s) {
        String v=s.toLowerCase(Locale.ROOT);
        if(v.contains("weather")||v.contains("rain")||v.contains("cyclone")) return "Weather";
        if(v.contains("police")||v.contains("crime")||v.contains("traffic")) return "Public Safety";
        if(v.contains("school")||v.contains("student")||v.contains("education")) return "Education";
        if(v.contains("health")||v.contains("hospital")||v.contains("medical")) return "Health";
        if(v.contains("farmer")||v.contains("agriculture")||v.contains("crop")) return "Agriculture";
        if(v.contains("government")||v.contains("district")||v.contains("collector")) return "Official Updates";
        return "District Updates";
    }
    private String child(Element parent,String name) {
        NodeList nodes=parent.getElementsByTagName(name);
        if(nodes.getLength()==0) nodes=parent.getElementsByTagNameNS("*",name.contains(":")?name.substring(name.indexOf(':')+1):name);
        return nodes.getLength()==0?"":nodes.item(0).getTextContent();
    }
    private String safeHost(String url) { try{return URI.create(url).getHost();}catch(Exception e){return "configured source";} }

    public List<ExternalFeedItem> publicItems(){ return repository.findByStatusInOrderByPublishedAtDesc(List.of("PUBLISHED")); }
    public List<ExternalFeedItem> pendingItems(){ return repository.findByStatusOrderByDiscoveredAtDesc("PENDING_REVIEW"); }
    public List<ExternalFeedItem> allItems(){ return repository.findAll(); }
    public void review(String id, boolean publish, String admin, String notes) {
        ExternalFeedItem item=repository.findById(id).orElseThrow(()->new IllegalArgumentException("External feed item not found."));
        item.setStatus(publish?"PUBLISHED":"REJECTED"); item.setReviewedBy(admin); item.setReviewedAt(Instant.now()); item.setAdminNotes(notes);
        repository.save(item);
    }
    public int refreshNow(){ refreshFeeds(); return pendingItems().size(); }
}
