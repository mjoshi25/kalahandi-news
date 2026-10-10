package in.asmj.kalahandiconnect.controller;

import in.asmj.kalahandiconnect.service.NewsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final NewsService service;
    public AdminController(NewsService service){this.service=service;}
    @GetMapping("/login") public String login(){ return "admin/login"; }
    @GetMapping({"", "/"}) public String dashboard(Model model){model.addAttribute("news",service.allNews());model.addAttribute("pendingCount",service.pendingNews().size());return "admin/dashboard";}
    @GetMapping("/news/{id}") public String review(@PathVariable String id,Model model){model.addAttribute("post",service.find(id));return "admin/review";}
    @PostMapping("/news/{id}/verify-mobile") public String verifyMobile(@PathVariable String id,@RequestParam(defaultValue="false") boolean verified,Authentication auth,RedirectAttributes ra){try{service.verifyMobile(id,verified,auth.getName());ra.addFlashAttribute("success","Mobile verification updated.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/admin/news/"+id;}
    @PostMapping("/news/{id}/verify-email") public String verifyEmail(@PathVariable String id,@RequestParam(defaultValue="false") boolean verified,Authentication auth,RedirectAttributes ra){try{service.verifyEmail(id,verified,auth.getName());ra.addFlashAttribute("success","Email verification updated.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/admin/news/"+id;}
    @PostMapping("/news/{id}/approve") public String approve(@PathVariable String id,@RequestParam(required=false) String notes,Authentication auth,RedirectAttributes ra){try{service.approve(id,auth.getName(),notes);ra.addFlashAttribute("success","News is now published.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/admin/news/"+id;}
    @PostMapping("/news/{id}/reject") public String reject(@PathVariable String id,@RequestParam(required=false) String notes,Authentication auth,RedirectAttributes ra){try{service.reject(id,auth.getName(),notes);ra.addFlashAttribute("success","Submission rejected.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/admin/news/"+id;}
    @PostMapping("/news/{id}/unpublish") public String unpublish(@PathVariable String id,@RequestParam(required=false) String notes,Authentication auth,RedirectAttributes ra){try{service.unpublish(id,auth.getName(),notes);ra.addFlashAttribute("success","News unpublished.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/admin/news/"+id;}
}
