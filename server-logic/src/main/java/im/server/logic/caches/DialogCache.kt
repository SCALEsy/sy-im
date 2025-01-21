package im.server.logic.caches

interface DialogCache {

    fun find(dialog_id: String): List<Int>

}