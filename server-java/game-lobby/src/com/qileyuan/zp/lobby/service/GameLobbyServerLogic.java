package com.qileyuan.zp.lobby.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.qileyuan.zp.lobby.proxy.GameLobbyServerProxy;

public class GameLobbyServerLogic {
	static Logger log = Logger.getLogger(GameLobbyServerLogic.class);

	private static GameLobbyServerLogic serverLogic = new GameLobbyServerLogic();
	private GameLobbyServerProxy proxy = null;

	private Map<Integer, Player> playerMap; // <userId, player>

	private GameLobbyServerLogic() {
		proxy = new GameLobbyServerProxy();
		playerMap = new HashMap<Integer, Player>();
		
		startAliveCheck();
	}

	public static GameLobbyServerLogic getInstance() {
		return serverLogic;
	}

	public void enterLobby(int userId, String userName, int userLevel, int equipedWeaponTypeId, float[] position) {
		if(playerMap.containsKey(userId)){
			return;
		}
		
		log.debug("["+userName+"] Enter Lobby");
		
		//add new player into map
		Player player = new Player();
		player.Id = userId;
		player.name = userName;
		player.level = userLevel;
		player.equipedWeaponTypeId = equipedWeaponTypeId;
		player.position = position;
		playerMap.put(userId, player);
		
		int[] receiverIds = getReceiverIds(userId);
		if(receiverIds != null && receiverIds.length > 0){
			proxy.addPlayer(receiverIds, userId, userName, userLevel, equipedWeaponTypeId, position);
		}
		
		List<Integer> userIdList = new ArrayList<Integer>();
		List<String> userNameList = new ArrayList<String>();
		List<Integer> userLevelList = new ArrayList<Integer>();
		List<Integer> equipedWeaponTypeIdList = new ArrayList<Integer>();
		List<Float> userPositionList = new ArrayList<Float>();
		
		for (int id : playerMap.keySet()) {
			if (userId != id) {
				Player otherPlayer = playerMap.get(id);

				userIdList.add(otherPlayer.Id);
				userNameList.add(otherPlayer.name);
				userLevelList.add(otherPlayer.level);
				equipedWeaponTypeIdList.add(otherPlayer.equipedWeaponTypeId);
				userPositionList.add(otherPlayer.position[0]);
				userPositionList.add(otherPlayer.position[1]);
				userPositionList.add(otherPlayer.position[2]);
			}
		}

		//get all other players' info in the lobby scene, and send back
		if(userIdList.size() > 0){
			int[] playerIds = ArrayUtils.toPrimitive((Integer[])userIdList.toArray(new Integer[0]));
			String[] playerNames = (String[])userNameList.toArray(new String[0]);
			int[] playerLevels = ArrayUtils.toPrimitive((Integer[])userLevelList.toArray(new Integer[0]));
			int[] equipedWeaponTypeIds = ArrayUtils.toPrimitive((Integer[])equipedWeaponTypeIdList.toArray(new Integer[0]));
			float[] playerPositions = ArrayUtils.toPrimitive((Float[])userPositionList.toArray(new Float[0]));
			
			proxy.sendPlayerInfos(new int[]{userId}, userId, playerIds, playerNames, playerLevels, equipedWeaponTypeIds, playerPositions);
		}
	}
	
	public void receiveMovement(int playerId, float[] target) {
		if (playerMap.containsKey(playerId)) {
			Player player = playerMap.get(playerId);
			player.position = target;
			
			int[] receiverIds = getReceiverIds(playerId);
			if(receiverIds != null && receiverIds.length > 0){
				proxy.sendMovement(receiverIds, playerId, target);
			}
		}
	}

	public void exitLobby(int playerId) {
		if (playerMap.containsKey(playerId)) {
			playerMap.remove(playerId);
			
			int[] receiverIds = getReceiverIds(playerId);
			if(receiverIds != null && receiverIds.length > 0){
				proxy.removePlayer(receiverIds, playerId);
			}
		}
	}
	
	public String getLobbyStatus(){
		StringBuffer sb = new StringBuffer();
		for (int userId : playerMap.keySet()) {
			Player player = playerMap.get(userId);
			sb.append(player.Id).append(",").append(player.name).append(",").append(player.level).append(",").
			append(player.equipedWeaponTypeId).append(",").append(Arrays.toString(player.position)).append(";");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length()-1);
		}
		
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
	
	private void startAliveCheck() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					if(playerMap == null || playerMap.size() == 0){
						return;
					}
					
					int[] playerIds = ArrayUtils.toPrimitive(playerMap.keySet().toArray(new Integer[0]));
					for (int playerId : playerIds) {
						if(!proxy.isPlayerLogin(playerId)){
							exitLobby(playerId);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 120 * 1000);
	}
	
	class Player {
		private int Id;
		private String name;
		private int level;
		private int equipedWeaponTypeId;
		private float[] position;
		
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
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		public int getEquipedWeaponTypeId() {
			return equipedWeaponTypeId;
		}
		public void setEquipedWeaponTypeId(int equipedWeaponTypeId) {
			this.equipedWeaponTypeId = equipedWeaponTypeId;
		}
		public float[] getPosition() {
			return position;
		}
		public void setPosition(float[] position) {
			this.position = position;
		}
		@Override
		public String toString() {
			return "Player [Id=" + Id + ", name=" + name + ", level=" + level + ", equipedWeaponTypeId=" + equipedWeaponTypeId + ", position=" + Arrays.toString(position) + "]";
		}
		
	}
}
