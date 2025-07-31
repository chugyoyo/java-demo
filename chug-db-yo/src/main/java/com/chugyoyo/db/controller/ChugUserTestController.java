package com.chugyoyo.db.controller;

import com.chugyoyo.db.mapper.ChugUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chug-user-test")
public class ChugUserTestController {

    @Autowired
    private ChugUserMapper chugUserMapper;

    @RequestMapping("/get-user-name-test-1")
    public String getUserName(@RequestParam(value = "id", required = true) Long id,
                              @RequestParam(value = "status", required = false) Integer status) {
        if (status == null) {
            return chugUserMapper.getUserName(id);
        }
        return chugUserMapper.getUserName(id, status);
    }
}
