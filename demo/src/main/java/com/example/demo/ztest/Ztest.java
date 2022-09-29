package com.example.demo.ztest;

import com.example.demo.product.DemoProducer;
import com.mzy.brightmq.beans.Message;
import com.mzy.brightmq.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  com.mzy.brightmq.ztest
 * @author BrightSpring 
 *7
 */
@Slf4j
@RestController
@RequestMapping("/ztest")
public class Ztest {
    @Autowired
    DemoProducer demoProducer;

    @GetMapping("/send")
    public void insertData(){
        demoProducer.createMessage();
    }

    @GetMapping("/saveBatch")
    public void insertBatchData(){
        demoProducer.createManyMessage();
    }

    @GetMapping("/createTopic")
    public void createTopic(){
        String topic="demo_message";
        demoProducer.createTopic(topic);
    }

    @GetMapping("/sendManyTopic")
    public void sendManyTopic(){
        demoProducer.sendManyTopic();
    }

    @GetMapping("/business")
    public void business(){
        demoProducer.business();
    }
}
