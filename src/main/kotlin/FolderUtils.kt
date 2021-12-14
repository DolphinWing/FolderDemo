import java.io.File

object FolderUtils {
    private val map = HashMap<String, List<String>>()

    fun buildMap(path: String = "~"): HashMap<String, List<String>> {
        map.clear() // clear all cache data
        val file = File(path)
        // println("search $path")
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
            val list = map.getOrDefault(file.extension, ArrayList())
            // println("${file.name} ${file.extension} ${list.size}")
            (list as? ArrayList)?.add(file.absolutePath)
            map[file.extension] = list
        } else {
            file.listFiles()?.forEach { f ->
                // println(f.absolutePath)
                buildFolderMap(f)
            }
        }
    }
}
