package in.asmj.kalahandiconnect.controller;

import in.asmj.kalahandiconnect.model.NewsPost;
import in.asmj.kalahandiconnect.model.NewsSubmissionForm;
import in.asmj.kalahandiconnect.service.NewsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PublicController {
    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    private final NewsService newsService;
    public PublicController(NewsService newsService){this.newsService=newsService;}
    @GetMapping("/") public String home(Model model){model.addAttribute("news",newsService.publicNews());return "index";}
    @GetMapping("/submit-news") public String submitForm(Model model){if(!model.containsAttribute("form"))model.addAttribute("form",new NewsSubmissionForm());return "submit-news";}
    @PostMapping("/submit-news") public String submit(@Valid @ModelAttribute("form") NewsSubmissionForm form, BindingResult result, RedirectAttributes redirect, Model model){
        if(result.hasErrors()) return "submit-news";
        try { NewsPost saved=newsService.submit(form); redirect.addFlashAttribute("referenceNumber",saved.getReferenceNumber()); return "redirect:/submission-received"; }
        catch(Exception ex){
            log.error("News submission failed. Check MongoDB connectivity and Cloudinary configuration if images were attached.", ex);
            model.addAttribute("uploadError", ex instanceof IllegalArgumentException || ex instanceof IllegalStateException
                    ? ex.getMessage() : "We could not save your submission. Please check that MongoDB is running and that Cloudinary credentials are configured if you attached photos, then try again.");
            return "submit-news";
        }
    }
    @GetMapping("/submission-received") public String received(){return "submission-received";}
    @GetMapping("/news/{id}") public String details(@PathVariable String id, Model model){
        NewsPost post=newsService.find(id);
        if(!"PUBLISHED".equals(post.getStatus()) || !post.isMobileVerified() || !post.isEmailVerified()) return "error/404";
        model.addAttribute("post",post); return "news-detail";
    }
}
