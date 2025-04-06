package cc.moreluck.quickboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @RequestMapping("/getHello")
    public String get(String param){
        return "hello";
    }
}
