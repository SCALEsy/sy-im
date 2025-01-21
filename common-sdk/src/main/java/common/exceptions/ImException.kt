package common.exceptions


class ImException(val code: Int, val msg: String) : RuntimeException(msg), java.io.Serializable {

    companion object {
        val NoDestIdException = ImException(20001, "no such dest id")
        val InvalidClientId = ImException(20002, "invalid msg client id")

        val InvalidCmdParam = ImException(20003, "invalid cmd param")
        val InvalidDialogId = ImException(20004, "invalid dialog id")

        val InvalidClient = ImException(20005, "invalid client msg")
        val UnknownError = ImException(20006, "unknown error")

        val CmdExeError = ImException(20007, "cmd exe error")
        val InvalidToken = ImException(20008, "invalid token")
        val LoginError = ImException(20008, "login error")

        val RPCException = ImException(10000, "rpc service call fail")
        val SendMsgError = ImException(30000, "send msg error")
        val SendTimeOut = ImException(30001, "send time out")

    }

    fun toData(): ExceptionData {
        return ExceptionData(code, msg)
    }
}