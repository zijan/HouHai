@echo off
set classpath=.
set classpath=%classpath%;lib\log4j-1.2.13.jar
set classpath=%classpath%;lib\xpp3_min-1.1.4c.jar
set classpath=%classpath%;lib\xstream-1.3.1.jar
set classpath=%classpath%;lib\commons-lang3-3.1.jar
set classpath=%classpath%;lib\commons-collections-3.2.1.jar
set classpath=%classpath%;lib\tatala.jar
set classpath=%classpath%;lib\game-gateway.jar
set classpath=%classpath%;lib\game-arena.jar
set classpath=%classpath%;cfg.gateway1

java com.qileyuan.zp.gateway.service.GameGatewayServer
