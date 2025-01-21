package sy.im.client.msg

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import java.io.File

class MapdbSaver(val name: String, val path: String) {

    private lateinit var db: DB
    fun <K, V> build(key: Serializer<K>, value: Serializer<V>): HTreeMap<K, V> {
        mkdir()
        val file = File("$path/$name.db")
        db = DBMaker.fileDB(file).fileChannelEnable().checksumHeaderBypass().fileMmapEnableIfSupported()
            .closeOnJvmShutdown().make()
        val map = db.hashMap(name, key, value).counterEnable().createOrOpen()
        return map
    }

    private fun mkdir() {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun close() {
        db.close()
    }
}