using System;
using QiLeYuan.FinalDefense.events;
using QiLeYuan.QGF.events;
using QiLeYuan.QGF.resources;
using QiLeYuan.QGF.util;
using UnityEngine;
using QiLeYuan.Tools.debug;
using com.qileyuan.zp.arena.proxy;

namespace QiLeYuan.FinalDefense.network {
    public class NetworkManager {

        private static NetworkManager mInstance = new NetworkManager();
        private ClientSenderProxy mClientSenderProxy = null;

        public bool IsConnected {
            get;
            set;
        }

        public static NetworkManager Instance {
            get {
                return mInstance;
            }
        }

        private NetworkManager() {
            mClientSenderProxy = new ClientSenderProxy();
        }

        #region send
        public void Login(int userID, string userName) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.Login(userID, userName);
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }

        public void Echo(int userID) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.Echo(userID);
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }

        public void Logout(int userID) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.Logout();
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }

        public void SendMessage(int userID, string message) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.SendMessage(userID, message);
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }

        public void EnterLobby(int userID, string userName, int userLevel, int equipedWeaponTypeId, Vector3 position) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.EnterLobby(userID, userName, userLevel, equipedWeaponTypeId, UnityTools.Vector3ToFloats(position));
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }

        public void SendMovement(int userID, Vector3 target) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.SendMovement(userID, UnityTools.Vector3ToFloats(target));
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }

        public void ExitLobby(int userID) {
            if (IsConnected) {
                try {
                    mClientSenderProxy.ExitLobby(userID);
                } catch (Exception) {
                    IsConnected = false;
                }
            }
        }
        #endregion

        #region receive
        public void ReceiveMessage(int userId, string message) {
            if (IsConnected) {
                ClientReceiveEvent clientReceiveEvent = new ClientReceiveEvent();
                clientReceiveEvent.SetData(new object[] { ClientReceiveEvent.Type_ReceiveMessage, userId, message });
                EventManager.Instance.PublishEvent(clientReceiveEvent);
            }
        }

        public void AddPlayer(int playerId, string playerName, int playerLevel, int equipedWeaponTypeId, float[] position) {
            if (IsConnected) {
                ClientReceiveEvent clientReceiveEvent = new ClientReceiveEvent();
                clientReceiveEvent.SetData(new object[] { ClientReceiveEvent.Type_AddPlayer, playerId, playerName, playerLevel, equipedWeaponTypeId, UnityTools.ArrayToVector3(position) });
                EventManager.Instance.PublishEvent(clientReceiveEvent);
            }
        }

        public void ReceiveMovement(int playerId, float[] target) {
            if (IsConnected) {
                ClientReceiveEvent clientReceiveEvent = new ClientReceiveEvent();
                clientReceiveEvent.SetData(new object[] { ClientReceiveEvent.Type_ReceiveMovement, playerId, UnityTools.ArrayToVector3(target) });
                EventManager.Instance.PublishEvent(clientReceiveEvent);
            }
        }

        public void ReceivePlayerInfos(int[] playerIds, string[] playerNames, int[] playerLevels, int[] equipedWeaponTypeIds, float[] playerPositions) {
            if (IsConnected) {
                Vector3[] positions = new Vector3[playerIds.Length];
                for (int i = 0; i < playerIds.Length; i++) {
                    float[] pos = new float[] { playerPositions[i * 3], playerPositions[i * 3 + 1], playerPositions[i * 3 + 2] };
                    positions[i] = UnityTools.ArrayToVector3(pos);
                }
                ClientReceiveEvent clientReceiveEvent = new ClientReceiveEvent();
                clientReceiveEvent.SetData(new object[] { ClientReceiveEvent.Type_ReceivePlayerInfos, playerIds, playerNames, playerLevels, equipedWeaponTypeIds, positions });
                EventManager.Instance.PublishEvent(clientReceiveEvent);
            }
        }

        public void RemovePlayer(int playerId) {
            if (IsConnected) {
                ClientReceiveEvent clientReceiveEvent = new ClientReceiveEvent();
                clientReceiveEvent.SetData(new object[] { ClientReceiveEvent.Type_RemovePlayer, playerId });
                EventManager.Instance.PublishEvent(clientReceiveEvent);
            }
        }
        #endregion
    }
}
