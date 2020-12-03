package mobi.cangol.web.pecker.view;

import mobi.cangol.web.pecker.core.model.AppServer;
import mobi.cangol.web.pecker.core.service.AppServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ServerController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppServerService appServerService;

    @GetMapping(value = "/server")
    public String list(Model model, HttpServletRequest request) {
        model.addAttribute("servers", appServerService.getAllList());
        return "server";
    }

    @GetMapping(value = "/server/edit")
    public String edit(long id,Model model, HttpServletRequest request) {
        model.addAttribute("server", appServerService.get(id));
        return "server_add";
    }

    @GetMapping(value = "/server/add")
    public String add(Model model, HttpServletRequest request) {
        model.addAttribute("server", new AppServer());
        return "server_add";
    }

    @PostMapping(value = "/server/save")
    public String save(AppServer appServer, HttpServletRequest request) {
        appServerService.save(appServer);
        return "redirect:/server";
    }

}

