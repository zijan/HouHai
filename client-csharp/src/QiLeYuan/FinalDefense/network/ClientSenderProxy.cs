using QiLeYuan.Tatala.executor;
using QiLeYuan.Tatala.proxy;
using QiLeYuan.Tatala.socket.client;
using QiLeYuan.Tatala.socket.to;

namespace QiLeYuan.FinalDefense.network {
    public class ClientSenderProxy {

        private string ExecuteClientCallMethodName = "executeClientCall";
        private TransferObjectFactory transferObjectFactory = new TransferObjectFactory(GameSetting.Server_Name, true);

        public ClientSenderProxy() {
            DefaultProxy clientDefaultProxy = new ClientReceiverProxy();
            transferObjectFactory.registerServerCallProxy(clientDefaultProxy);
        }

        public void Login(int userID, string userName) {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(ExecuteClientCallMethodName);
            to.putString("login"); //callee method
            to.putInt(userID);
            to.putString(userName);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }

        public void Echo(int userID) {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(ExecuteClientCallMethodName);
            to.putString("echo"); //callee method
            to.putInt(userID);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }

        public void Logout() {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(LongClientSession.METHOD_CLOSE);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }

        public void SendMessage(int userID, string message) {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(ExecuteClientCallMethodName);
            to.putString("receiveMessage"); //callee method
            to.putInt(userID);
            to.putString(message);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }

        public void EnterLobby(int userID, string userName, int userLevel, int equipedWeaponTypeId, float[] position) {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(ExecuteClientCallMethodName);
            to.putString("enterLobby"); //callee method
            to.putInt(userID);
            to.putString(userName);
            to.putInt(userLevel);
            to.putInt(equipedWeaponTypeId);
            to.putFloatArray(position);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }

        public void SendMovement(int userID, float[] target) {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(ExecuteClientCallMethodName);
            to.putString("receiveMovement"); //callee method
            to.putInt(userID);
            to.putFloatArray(target);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }

        public void ExitLobby(int userID) {
            NewTransferObject to = transferObjectFactory.createNewTransferObject();
            to.setCalleeMethod(ExecuteClientCallMethodName);
            to.putString("exitLobby"); //callee method
            to.putInt(userID);
            to.registerReturnType(TransferObject.DATATYPE_VOID);
            ServerExecutor.execute(to);
        }
    }
}
