package com.baijiacms.qsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 11:08
 */
@Controller
@RequestMapping("/template")
public class TemplateController {
    @GetMapping({"", "/", "/index"})
    public String view(ModelMap modelMap) {

        return "/index";
    }
    @GetMapping("/loading")
    public String loading() {

        return "/loading";
    }
    @GetMapping("/gameIndex")
    public String gameIndex() {

        return "/gameIndex";
    }

    @GetMapping("/engineConsolePage")
    public String engineConsolePage() {

        return "/console";
    }
    @GetMapping("/engineHtmlPage")
    public String engineHtmlPage() {

        return "/html";
    }
    @GetMapping("/engineActionPage")
    public String engineActionPage() {

        return "/action";
    }

    @GetMapping("/engineUserPage")
    public String engineUserPage() {

        return "/user";
    }
}
