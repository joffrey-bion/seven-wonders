package org.luxons.sevenwonders.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    // sent to subscribers of /topic/test1
    @MessageMapping("/test1")
    public String test1(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        System.out.println("No annotation - " + headerAccessor.getSessionId());

        return "success test1";
    }

    // sent to subscribers of /broadcast/test2
    @MessageMapping("/test2")
    @SendTo("/broadcast/test2")
    public String test2(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        System.out.println("@SendTo /broadcast/test2 - " + headerAccessor.getSessionId());

        return "success test2";
    }

    @MessageMapping("/test3")
    @SendToUser("/broadcast/test3")
    public String test3(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        System.out.println("@SendToUser /broadcast/test3 - " + headerAccessor.getSessionId());

        return "success test3";
    }

    @MessageMapping("/test4")
    @SendToUser("/test4")
    public String test4(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        System.out.println("@SendToUser /test4 - " + headerAccessor.getSessionId());

        return "success test4";
    }

    // sent to the caller user if he subscribed to /user/queue/test5
    // other subscribers of /user/queue/test5 are NOT notified
    @MessageMapping("/test5")
    @SendToUser
    public String test5(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        System.out.println("@SendToUser (no path) - " + headerAccessor.getSessionId());

        return "success test5";
    }

    // sent to the caller user if he subscribed to /user/queue/test6
    // other subscribers of /user/queue/test6 are NOT notified
    @MessageMapping("/test6")
    // TODO
    public String test6(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        // TODO
        System.out.println("@SendToUser (no path) - " + headerAccessor.getSessionId());

        return "success test6";
    }
}
