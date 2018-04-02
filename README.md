HouHai
======

HouHai is online game server framework, base on [Tatala](https://github.com/zijan/Tatala).

It was implemented by single-server pattern, all players can play together, and easy to expand server volume by adding new server when number of player increase.

Right now, the source code is just draft version. It is part of network codes of the online game – “Zombie Planet”. And it can’t be compiled.

“Zombie Planet”, the game’s address is http://rc.qzone.qq.com/1101064313. It is a social game published on Tencent QZone in China. It has online game feature. Players can chat and kill each other. The client side of game is Unity3D using C#. The server side is Java. HouHai is the server framework implements network function. So HouHai repository includes two part, C# client and Java server.

Later, I am going to finish this framework and create a small demo.

设计思想：
在没有修改服务器系统设置的情况下，一台机器所能承载的最大连接数是有限制的，比如8000个。通常一个服务器就代表了一个“区”，即保持了所以的用户连接又包含了游戏逻辑，一般情况下各个区之间的玩家是不通信的。非分区设计就是，把保持用户连接的功能从包含游戏逻辑的服务器中分离，单独做成多个连接服务器的并联（看HouHai里的Gateway就是连接服务器），它只保持玩家的网络连接，不包含游戏逻辑，负责传递客户端与游戏逻辑服务器（HouHai里的Longin和Lobby）的通信。这样不需要玩家选择游戏分区，所有玩家可以互相通信，只要加足够多的连接服务器，就能承载足够多的用户。
