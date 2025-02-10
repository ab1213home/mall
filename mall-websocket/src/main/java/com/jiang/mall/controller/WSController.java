/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WSController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    public ResponseResult<Object> hello(String requestMessage) {
        System.out.println("接收消息：" + requestMessage);
        return ResponseResult.okResult("服务端接收到你发的：" + requestMessage);
    }

    @GetMapping("/sendMsgByUser")
    @ResponseBody
    public Object sendMsgByUser(String token, String msg) {
        simpMessagingTemplate.convertAndSendToUser(token, "/msg", msg);
        return "success";
    }

    @GetMapping("/sendMsgByAll")
    @ResponseBody
    public Object sendMsgByAll(String msg) {
        simpMessagingTemplate.convertAndSend("/topic", msg);
        return "success";
    }

}

//通过@MessageMapping 来暴露节点路径，有点类似 @RequestMapping。注意这里虽然写的是 hello ，但是我们客户端调用的真正地址是 /app/hello。 因为我们在上面的 config 里配置了registry.setApplicationDestinationPrefixes("/app")。
//@SendTo这个注解会把返回值的内容发送给订阅了/topic/hello 的客户端，与之类似的还有一个@SendToUser 只不过他是发送给用户端一对一通信的。这两个注解一般是应答时响应的，如果服务端主动发送消息可以通过simpMessagingTemplate类的convertAndSend方法。注意simpMessagingTemplate.convertAndSendToUser(token, "/msg", msg) ，联系到我们上文配置的 registry.setUserDestinationPrefix("/user/"),这里客户端订阅的是/user/{token}/msg,千万不要搞错。
