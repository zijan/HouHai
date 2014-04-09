using System;
using com.qileyuan.zp.arena.proxy;
using QiLeYuan.Tatala.proxy;
using QiLeYuan.Tatala.socket.to;

namespace QiLeYuan.FinalDefense.network {
    public class ClientReceiverProxy : DefaultProxy {
        public override Object execute(TransferObject baseto) {
            NewTransferObject to = (NewTransferObject)baseto;
            string calleeMethod = to.getCalleeMethod();
            if (calleeMethod.Equals("receiveMessage")) {
                int playerId = to.getInt();
                string message = to.getString();
                NetworkManager.Instance.ReceiveMessage(playerId, message);

            } else if (calleeMethod.Equals("addPlayer")) {
                int playerId = to.getInt();
                string playerName = to.getString();
                int playerLevel = to.getInt();
                int equipedWeaponTypeId = to.getInt();
                float[] position = to.getFloatArray();
                NetworkManager.Instance.AddPlayer(playerId, playerName, playerLevel, equipedWeaponTypeId, position);

            } else if (calleeMethod.Equals("receiveMovement")) {
                int playerId = to.getInt();
                float[] target = to.getFloatArray();
                NetworkManager.Instance.ReceiveMovement(playerId, target);

            } else if (calleeMethod.Equals("receivePlayerInfos")) {
                to.getInt(); //ignore userId 
                int[] playerIds = to.getIntArray();
                string[] playerNames = to.getStringArray();
                int[] playerLevels = to.getIntArray();
                int[] equipedWeaponTypeIds = to.getIntArray();
                float[] playerPositions = to.getFloatArray();
                NetworkManager.Instance.ReceivePlayerInfos(playerIds, playerNames, playerLevels, equipedWeaponTypeIds, playerPositions);

            } else if (calleeMethod.Equals("removePlayer")) {
                int playerId = to.getInt();
                NetworkManager.Instance.RemovePlayer(playerId);

            }

            return null;
        }
    }
}
