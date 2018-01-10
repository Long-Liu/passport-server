package org.infinity.passport.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping(value = "/system/redis/admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @Secured(Authority.DEVELOPER)
    public void redirectToRedisAdmin(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getRedis().getAdminUrl());
    }

    @RequestMapping(value = "/system/scheduler/admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @Secured(Authority.DEVELOPER)
    public void redirectToScheduler(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getScheduler().getAdminUrl());
    }
}
