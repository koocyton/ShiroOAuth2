# 狼人杀 Server  < 含小程序登录的接口 >

#### 用到的库
``` html
Undertow ( Http Server )
Undertow-Websocket ( Websocket Server )
Spring ( 注入 )
MyBatis ( 数据库操作 )
HikariCP ( 数据连接池 )
fastjson ( Json 序列化 )
Jedis ( Cache User Session )
slf4j ( 日志 )
freemarker ( 模板，用于页面调试 )
mapstruct ( DTO )
lombok ( 简化 Getter Setter )
webjars ( 集成用到的 CSS JS )
async-http-client ( 有发现内存不断上涨，暂时换用非异步的网络请求 )
```

#### 特点

* 使用 Undertow 为高性能服务器，gradle release 打出 jar 包后
通过 java -jar wxlrs-server-1.0.jar & 即可启动服务

* 配置文件换从 java 文件，而非 xml ，加快启动速度，两秒 ( Netty + Guice 更快，但是没有扫描 )

* 替换 jks 文件，可以启动 https 服务

#### 例子 
``` html
https://wxlrs.gauss.doopp.com/helper/robot
```

#### Nginx + RTMP 服的一些配置
``` html
如果支持 rtmp 同时多个连接，比较好处理，一个推流，对同房间的其他人拉流
rtmp {
    server {
        listen 1935;
        application live {
            live on;
        }
    }
}

如果有支持一个连接推流，一个连接拉流，需要转码
参考 http://colobu.com/2016/07/13/Setup-Nginx-and-RTMP-module/
以及 ffmpeg 对音频的合并

flazr 是基于 netty 的推拉流服务器，基于 netty 二次开发 rtmp 服是一个很好的选择
```


#### 微信小程序的推拉流
``` html
拉流 wx.createLivePlayerContext
推流 wx.createLivePusherContext

关于可能出现没声音的情况
   <live-push> 默认不自动推流，进入页面后，靠 wx.createLivePusherContext 启动
   <live-pull> 默认不自己拉流，同上， 靠 wx.createLivePlayerContext 启动
```
