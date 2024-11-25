package org.myproject.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.myproject.shortlink.project.common.convention.result.Result;
import org.myproject.shortlink.project.common.convention.result.Results;
import org.myproject.shortlink.project.service.URLTitleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private final URLTitleService urlTitleService;

    @SneakyThrows
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitle(@RequestParam("url") String url){
        return Results.success(urlTitleService.getTitle(url));
    }
}
