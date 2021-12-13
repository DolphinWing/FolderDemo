import java.io.File

object FolderUtils {
    private val map = HashMap<String, Int>()

    fun buildMap(path: String = "~"): HashMap<String, Int> {
        map.clear() // clear all cache data
        val file = File(path)
        if (file.exists()) {
            file.listFiles()?.forEach { f ->
                // println(f.absolutePath)
                buildFolderMap(f)
            }
        }
        return map
    }

    private fun buildFolderMap(file: File) {
        if (file.isFile) {
            // println("${file.absolutePath} ${file.extension}")
            val now = map.getOrDefault(file.extension, 0)
            println("${file.name} ${file.extension} $now")
            map[file.extension] = now + 1
        } else {
            file.listFiles()?.forEach { f ->
                // println(f.absolutePath)
                buildFolderMap(f)
            }
        }
    }
}
