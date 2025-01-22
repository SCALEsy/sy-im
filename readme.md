这是一个即时通讯的IM后端系统，包含分布式的gateway,logic sever,数据存储，和基础信息服务.
采用netty4,dubbo+springboot,redis,rocketmq,mysql,zookeeper实现，
存储包含部分分表，使用sharding-jdbc(未完善).

已实现：
1.单聊

2.群聊（扩散写+pull+push）

3,简单客户端

4，可使用tiny-id替换现在的id策略
原理和计划

1.ack处理完善(down)

2.消息可靠性（down）

3.消息一致性(id,down)(客户端未实现)

4.消息落地存储(mysql,down)

5,消息缓存（down）

6.client消息推送与消费模型重写(down)

7，群聊天实现(down)

8.图片，语音

9.消息已读机制(保存id最大,记录last_id)(down)

10.exception 实现(down)

11.关系网消息过滤(no)

12.模块分离，构建罗技层(down)

13.客户端逻辑重写

14.mysql数据落地慢(已分表，未测试)

15.丢消息。稳定复现(确认为netty channel isWriteable 问题 down)

16.client 高并发下ping无回包,未找到原因 (暂估为高并发下，channel只在
一个线程内顺序接受，导致ping包逻辑排队到很后面，而不是不发)(已采用漏桶算法实现限制并发)

17.数据落地分表分库，做本地缓存(已分表,未缓存)

18.配置中心

19,android端