package mobi.cangol.web.pecker.view;

import mobi.cangol.web.pecker.core.model.App;
import mobi.cangol.web.pecker.core.service.AppServerService;
import mobi.cangol.web.pecker.core.service.AppService;
import mobi.cangol.web.pecker.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AppController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppService appService;
    @Autowired
    private AppServerService appServerService;

    @GetMapping(value = "/")
    public String index(String agent, Model model, HttpServletRequest request) {
        log.debug("app agent={}",agent);
        agent = WebUtils.filterAgent(agent,request);
        List<App> apps=appService.findByAgent(agent);
        model.addAttribute("agent", agent);
        model.addAttribute("apps", apps);
        return "index";
    }

    @GetMapping(value = "/app")
    public String list(Model model, HttpServletRequest request) {
        List<App> apps=appService.getAllList();
        model.addAttribute("apps", apps);
        return "app";
    }

    @PostMapping(value = "/app/save")
    public String save(App app,Model model, HttpServletRequest request) {
        appService.save(app);
        return "redirect:/app";
    }

    @GetMapping(value = "/app/add")
    public String add(Model model, HttpServletRequest request) {
        model.addAttribute("app", new App());
        model.addAttribute("servers", appServerService.getAllList());
        return "app_add";
    }

    @GetMapping(value = "/app/edit")
    public String edit(long id,Model model, HttpServletRequest request) {
        model.addAttribute("app", appService.get(id));
        model.addAttribute("servers", appServerService.getAllList());
        return "app_add";
    }

    @GetMapping(value = "/app/delete")
    public String delete(long id,Model model, HttpServletRequest request) {
        appService.delete(id);
        return "redirect:/app";
    }
}

