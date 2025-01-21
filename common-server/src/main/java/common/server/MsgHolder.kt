package common.server

import common.beans.Msg

data class MsgHolder(var msg: Msg? = null) : java.io.Serializable {
}