package in.asmj.kalahandiconnect.controller;

import in.asmj.kalahandiconnect.service.ExternalFeedService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/live-feed")
public class AdminLiveFeedController {
    private final ExternalFeedService service;
    public AdminLiveFeedController(ExternalFeedService service){this.service=service;}
    @GetMapping public String queue(Model model){
        model.addAttribute("items",service.pendingItems());
        return "admin/live-feed-review";
    }
    @PostMapping("/refresh") public String refresh(RedirectAttributes ra){
        try { service.refreshNow(); ra.addFlashAttribute("success","Feed refresh completed. Newly discovered items are pending review."); }
        catch(Exception e){ ra.addFlashAttribute("error","Feed refresh failed. Check configured RSS URLs and application logs."); }
        return "redirect:/admin/live-feed";
    }
    @PostMapping("/{id}/publish") public String publish(@PathVariable String id,@RequestParam(required=false) String notes, Authentication auth, RedirectAttributes ra){
        try { service.review(id,true,auth.getName(),notes); ra.addFlashAttribute("success","External item published with its original source link."); }
        catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}
        return "redirect:/admin/live-feed";
    }
    @PostMapping("/{id}/reject") public String reject(@PathVariable String id,@RequestParam(required=false) String notes, Authentication auth, RedirectAttributes ra){
        try { service.review(id,false,auth.getName(),notes); ra.addFlashAttribute("success","External item rejected.");}
        catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}
        return "redirect:/admin/live-feed";
    }
}
