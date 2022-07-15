package com.atguigu.gmall.order.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanyudong
 * @date 2022/7/14 8:42
 */

public class ObserverTest {


    public static void main(String[] args) {
        //1.发布者
        MessagePublisher publisher = new MessagePublisher();
        //2.监听者
        PersonSubscriber list = new PersonSubscriber("lisi");
        publisher.guanzhu(list);
        PersonSubscriber zhangsan = new PersonSubscriber("zhangsan");
        publisher.guanzhu(zhangsan);
        //3.发消息
        publisher.publishVideo("Java入门到精通");

    }


}

//1、发布者；   封装、继承、多态
@NoArgsConstructor
@Data
class MessagePublisher{
    //发布者得知道有哪些观察者
   List<PersonSubscriber> fans =   new ArrayList<>();
    //来获取观察者
    public void guanzhu(PersonSubscriber fan){
        fans.add(fan);
    }
    //发布消息
    public void publishVideo(String name){
        System.out.println("视频发布："+ name);
        //遍历所有观察者通知他们；
        for (PersonSubscriber fan : fans) {
            fan.listenMessage(name);
        }

    }


}
//2、观察者
@AllArgsConstructor
@Data
class PersonSubscriber{
    private String userName;
    public void listenMessage(String name){
        System.out.println("【"+userName+"】收到消息："+name);
    }

}
