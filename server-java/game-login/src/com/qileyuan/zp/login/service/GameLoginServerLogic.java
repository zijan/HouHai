package com.qileyuan.zp.login.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.qileyuan.zp.login.proxy.GameLoginServerProxy;

public class GameLoginServerLogic {
	static Logger log = Logger.getLogger(GameLoginServerLogic.class);

	private static GameLoginServerLogic serverLogic = new GameLoginServerLogic();
	private GameLoginServerProxy proxy = null;

	private Map<Integer, Player> playerMap; // <userId, player>

	private GameLoginServerLogic() {
		proxy = new GameLoginServerProxy();
		playerMap = new HashMap<Integer, Player>();
	}

	public static GameLoginServerLogic getInstance() {
		return serverLogic;
	}

	public void login(int gatewayId, int userId, String userName) {
		if(playerMap.containsKey(userId)){
			return;
		}
		
		log.debug("["+userName+"] Longin");
		
		//add new player into map
		Player player = new Player();
		player.gatewayId = gatewayId;
		player.Id = userId;
		player.name = userName;
		playerMap.put(userId, player);
		
		//TODO
		int[] receiverIds = getReceiverIds(userId);
		if(receiverIds != null && receiverIds.length > 0){
			proxy.sendMessage(receiverIds, userId, player.name + " ÉÏÏß");
		}
	}
	
	public void receiveMessage(int userId, String message) {
		if (playerMap.containsKey(userId)) {
			log.debug("["+playerMap.get(userId).name+"]: "+message);
			
			int[] receiverIds = getReceiverIds(userId);
			if(receiverIds != null && receiverIds.length > 0){
				proxy.sendMessage(receiverIds, userId, message);
			}
		}
	}

	public void removePlayer(int userId) {
		if (playerMap.containsKey(userId)) {
			playerMap.remove(userId);
		}
		//user logout should exit lobby
		proxy.exitLobby(userId);
		//user logout should exit arena
		proxy.exitArena(userId);
		//log.debug(userId + "|" +playerMap);
	}
	
	public String getLoginStatus(){
		StringBuffer sb = new StringBuffer();
		for (int userId : playerMap.keySet()) {
			Player player = playerMap.get(userId);
			sb.append(player.Id).append(",").append(player.gatewayId).append(",").append(player.name).append(";");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length()-1);
		}

		//log.debug("LoginStatus: "+sb.toString());
		
		return sb.toString();
	}
	
	private int[] getReceiverIds(int senderId){
		int[] receiverIds = null;
		List<Integer> idList = new ArrayList<Integer>();
		for(int id : playerMap.keySet()){
			if(senderId != id){
				idList.add(id);
			}
		}
		receiverIds = ArrayUtils.toPrimitive(idList.toArray(new Integer[0]));
		
		return receiverIds;
	}
	
	public boolean isPlayerLogin(int userId){
		return playerMap.containsKey(userId);
	}
	
	class Player {
		private int Id;
		private String name;
		private int gatewayId;
		public int getId() {
			return Id;
		}
		public void setId(int id) {
			Id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getGatewayId() {
			return gatewayId;
		}
		public void setGatewayId(int gatewayId) {
			this.gatewayId = gatewayId;
		}
		@Override
		public String toString() {
			return "Player [Id=" + Id + ", name=" + name + ", gatewayId=" + gatewayId + "]";
		}
	}
}
