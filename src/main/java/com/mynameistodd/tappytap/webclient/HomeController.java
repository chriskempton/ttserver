package com.mynameistodd.tappytap.webclient;

import java.util.Map;
import java.util.HashMap;

import com.mynameistodd.tappytap.server.data.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/home")
public class HomeController {

    @RequestMapping(method={RequestMethod.GET})
    public ModelAndView homeOutput() {
        return new ModelAndView("home", getHomePropertiesMap());
    }

    private static Map<String, Object> getHomePropertiesMap() {
        Map<String, Object> model = new HashMap<>();
        model.put("deviceCount", Device.getTotalCount());
        return model;
    }
}