package in.asmj.kalahandiconnect.controller;

import in.asmj.kalahandiconnect.service.ExternalFeedService;
import in.asmj.kalahandiconnect.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LiveFeedController {
    private final ExternalFeedService externalFeedService;
    private final NewsService newsService;
    public LiveFeedController(ExternalFeedService externalFeedService, NewsService newsService) {
        this.externalFeedService=externalFeedService; this.newsService=newsService;
    }
    @GetMapping("/live-feed")
    public String liveFeed(Model model) {
        model.addAttribute("localNews",newsService.publicNews());
        model.addAttribute("externalNews",externalFeedService.publicItems());
        return "live-feed";
    }
}
