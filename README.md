中国境内仓库： https://gitee.com/mingzy/bright-mq.git
### 介绍
- BrightMQ是基于mysql-binlog的轻量级高性能开源的消息队列中间件，利用数据库特性，极少的代码实现服务间的异步解耦、数据一致性、重复消费等问题。
- 使用独立数据源，无业务侵入。
- 一般企业在使用MQ时，主要解决异步解耦与数据一致性问题，而关于性能问题反而不是MQ首要的能力。
- 不超过独立数据库写入瓶颈的消息队列需求，本方案适合。

### 解决的问题
- 企业在使用MQ来解决实际问题的成本越来越高，系统复杂度增加，BrightMQ大大的加强了消息队列的易用性。
- 增加可维护性，不依赖外部消息中心，开发者会使用mysql就会很方便的管理消息。 
- 简化系统复杂度。 将消息消费分散到各个业务服务，只需要一个数据库来存储。
- 横向可扩展，一个表存储一种消息类型，增加表类似增加消息业务场景或topic。
- 业务解耦，使用独立数据源，无业务侵入。
- 本源码仅作为抛砖引玉，可自行根据源码扩展，开发者可自行优化与完善更多适合自己业务场景的消息能力。

### 架构图
![输入图片说明](sql/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20220921100902.png)

### 使用教程
- 在项目中引入spring-boot-brightmq-starter的jar包后即拥有了生产消息、消费消息的，轻量易用，减少系统复杂度，减少外部依赖。
- 源码在core文件夹下，打包后即生成上述的spring-boot-brightmq-starter的jar包。
- 开源协议Apache License 2.0。

### 1、引入依赖  （可参考demo）
- 本地引用
```
        <!-- 此处为本地引用jar包方式 -->
        <dependency>
            <groupId>com.mzy</groupId>
            <artifactId>spring-boot-brightmq-starter</artifactId>
            <version>1.0.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/spring-boot-brightmq-starter.jar</systemPath>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.shyiko</groupId>
            <artifactId>mysql-binlog-connector-java</artifactId>
            <version>0.21.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.7.3</version>
                <!-- 本地引用jar包时需要配置。若非本地jar引用模式则可无需此配置 -->
                <configuration>
                    <includeSystemScope>true</includeSystemScope>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
- 公共仓库引用 （作者比较懒，还没搞公库审核，推荐使用上边本地引用方式，可参考demo）
```
        <dependency>
            <groupId>com.mzy</groupId>
            <artifactId>spring-boot-brightmq-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
        
        <!-- 其他依赖引用同上 -->
```
### 2、配置 （可参考demo）
- 需要独立的存储消息的数据库配置。主要多了一个binlog监听的serviceId配置。执行脚本查看自己数据库的serviceId: `show variables like 'server_id'`
```
# 服务原本的业务数据库配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 此处需要注意，原来若是url则需改成jdbc-url
    jdbc-url: jdbc:mysql://****:3306/ming_test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
    username: ****
    password: ****


# mq消息存储数据源
bright-mq:
  driver-class-name: com.mysql.cj.jdbc.Driver
  jdbc-url: jdbc:mysql://****:3306/bright-mq?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
  username: ****
  password: ****
  # 监听binlog的service-id。执行sql脚本 show variables like 'server_id'; 进行查看
  binlog-service-id: 20571336
  #消费失败的消息，重试消费任务配置
  consume-task:
    # 重试次数：不配置则默认重试10次
    max-failed-times: 15
    # 重试时间间隔：不配置则默认每次执行结束后10秒再次重试,单位毫秒
    fixed-delay: 2000000

```
- 启动类上增加扫描。
```
@ComponentScan({"com.mzy.brightmq"})
```
### 3、使用 （可参考demo）
- 发消息
```
    @Autowired
    private MessageService messageService;
    
        //创建一个default_message存储的消息
        Message message= new Message()
                //必填字段，指定存储表（类似topic，一个表存储同一个类型的消息）
                .setTableName("default_message")
                //必填字段
                .setContent("任意的消息")
                //选填字段，自定义的业务唯一ID
                .setBizId(UUID.randomUUID().toString());
        //发送消息
        messageService.sendMessage(message);
```

- 消费消息
```

/**
 *  消费者消费消息，必须实现接口BrightMQConsumer。并在@Service注解中指定topic（表名）
 * @author BrightSpring 
 *15
 */
@Slf4j
@Service("demo_message")
public class DemoConsumer implements BrightMQConsumer {
    @Override
    public boolean consumeMessage(Message message) {
        log.info("检测到一条消息，我要消费了={}",message);
        return true;
    }
}
```
### 开源协议： Apache License2.0

借助本项目的代码或思想进行二次开发或二次开源，请记得帮助宣传，开源不易，感谢！

欢迎大家star与收藏。

完全开源，请作者喝杯茶吧！

| 请作者喝杯茶吧  | 联系作者  |
|---|---|
|![输入图片说明](sql/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20220919164423.png)|![输入图片说明](sql/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20220919163724.jpg)|


