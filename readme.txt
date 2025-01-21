计划
1.ack处理完善(down)
2.消息可靠性（down）
3.消息一致性(id,down)(sort client,?)
4.消息落地存储(mysql,down)
5,消息缓存（how）
6.client消息推送与消费模型重写
7，群聊天实现
8.图片，语音
9.消息已读机制(保存id最大,记录last_id)
10.exception 实现
11.关系网消息过滤
12.模块分离，构建罗技层
13.客户端逻辑重写
14.mysql数据落地慢
15.丢消息。稳定复现(确认为netty channel isWriteable 问题，未解决)
16.client 高并发下ping无回包,未找到原因 (暂估为高并发下，channel只在一个线程内顺序接受，导致ping包逻辑排队到很后面，而不是不发)
17.数据落地分表分库，做本地缓存
18.配置中心