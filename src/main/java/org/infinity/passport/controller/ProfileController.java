package org.infinity.passport.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.entity.ProfileInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "系统环境")
public class ProfileController {

    @Autowired
    private Environment           env;

    @Autowired
    private ApplicationProperties applicationProperties;

    @ApiOperation("获取系统Profile")
    @RequestMapping(value = "/open-api/profile-info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfileInfo> getProfileInfo() {
        ProfileInfo profileInfo = new ProfileInfo(env.getActiveProfiles(), getRibbonEnv());
        return new ResponseEntity<ProfileInfo>(profileInfo, HttpStatus.OK);
    }

    private String getRibbonEnv() {
        String[] activeProfiles = env.getActiveProfiles();
        String[] displayOnActiveProfiles = applicationProperties.getRibbon().getDisplayOnActiveProfiles();

        if (displayOnActiveProfiles == null) {
            return null;
        }

        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);

        if (ribbonProfiles.size() > 0) {
            return ribbonProfiles.get(0);
        }
        return null;
    }
}
