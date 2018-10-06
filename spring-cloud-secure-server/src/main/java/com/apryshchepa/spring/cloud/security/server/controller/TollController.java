package com.apryshchepa.spring.cloud.security.server.controller;

import com.apryshchepa.spring.cloud.security.server.model.TollUsage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TollController {

    @RequestMapping("/tolldata")
    @PreAuthorize("#oauth2.hasScope('toll_read') and hasAuthority('ROLE_OPERATOR')")
    public List<TollUsage> tollData() {

        TollUsage instance1 = new TollUsage("200", "station150", "B65GT1W", "2016-09-30T06:31:22");
        TollUsage instance2 = new TollUsage("201", "station119", "AHY673B", "2016-09-30T06:32:50");
        TollUsage instance3 = new TollUsage("202", "station150", "ZN2GP0", "2016-09-30T06:37:01");

        List<TollUsage> tolls = new ArrayList<TollUsage>();
        tolls.add(instance1);
        tolls.add(instance2);
        tolls.add(instance3);

        return tolls;
    }
}
