package sy.im.client

import org.mapdb.Serializer
import sy.im.client.msg.MapdbSaver


fun main() {
    val saver = MapdbSaver("user", "./save").build(Serializer.STRING, Serializer.JAVA)
    saver["zxcz"] = "zxccz"
    saver.close()
}