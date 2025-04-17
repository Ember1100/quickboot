package cc.moreluck.quickboot.controller;

import cc.moreluck.quickboot.annotation.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @RequestMapping("/getHello")
    public String get(String param){
        return "hello";
    }


    @DistributeLock(scene = "1", key = "2")
    @RequestMapping("/getCache")
    public String getCache(Object param){
        log.info("param:{}",param);
        return "hello";
    }
}
