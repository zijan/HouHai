#!/bin/sh

classpath=.
classpath=$classpath:lib/log4j-1.2.13.jar
classpath=$classpath:lib/xpp3_min-1.1.4c.jar
classpath=$classpath:lib/xstream-1.3.1.jar
classpath=$classpath:lib/commons-lang3-3.1.jar
classpath=$classpath:lib/commons-collections-3.2.1.jar
classpath=$classpath:lib/tatala.jar
classpath=$classpath:lib/game-login.jar
classpath=$classpath:cfg.login

exec java -cp "$classpath" com.qileyuan.zp.login.service.GameLoginServer