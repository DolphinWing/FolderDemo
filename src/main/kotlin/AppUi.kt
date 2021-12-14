import androidx.compose.animation.Crossfade
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import javax.swing.JFileChooser

enum class UiState {
    Keys, Files
}

@Composable
@Preview
fun App() {
    var data by remember { mutableStateOf(Array(0) { Pair("", emptyList<String>()) }) }
    var state by remember { mutableStateOf(UiState.Keys) }
    var showKey by remember { mutableStateOf("") }

    MaterialTheme(
        colors = lightColors(
            primary = Color.Gray,
            primaryVariant = Color.DarkGray,
            secondary = Color.Cyan
        )
    ) {
        Crossfade(state) { s ->
            when (s) {
                UiState.Keys ->
                    KeyPane(
                        data = data,
                        onListChange = { data = it },
                        onDetailClick = { showKey = it; state = UiState.Files },
                    )
                UiState.Files ->
                    FilePane(
                        data = data,
                        onCancel = { state = UiState.Keys; showKey = "" },
                        key = showKey,
                    )
            }
        }
    }
}

@Composable
private fun KeyPane(
    data: Array<Pair<String, List<String>>>,
    onListChange: (Array<Pair<String, List<String>>>) -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: ((String) -> Unit)? = null,
) {
    val composeScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        SearchPane(
            modifier = Modifier.fillMaxWidth(),
            onSubmit = { text ->
                composeScope.launch {
                    val list = FolderUtils.buildMap(text).toSortedMap()
                        .map { entry -> Pair(entry.key, entry.value) }
                        .toTypedArray()
                    // println("complete: ${list.size}")
                    onListChange.invoke(list)
                }
            },
            initText = if (BuildConfig.DEBUG) "/home/jimmyhu/work/github/FolderDemo" else ""
        )
        KeyListPane(modifier = Modifier.weight(1f), list = data, onSubmit = onDetailClick)
    }
}

@Composable
private fun SearchPane(modifier: Modifier = Modifier, initText: String = "", onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf(initText) }
    val chooser = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text("Project directory")
            }
        )
        IconButton(onClick = {
            val ret = chooser.showOpenDialog(ComposeWindow())
            if (ret == JFileChooser.APPROVE_OPTION) {
                text = chooser.selectedFile.absolutePath
            }
        }) {
            Icon(Icons.Default.Search, contentDescription = null)
        }
        IconButton(onClick = { onSubmit.invoke(text) }) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
        }
    }
}

@Composable
private fun KeyListPane(
    modifier: Modifier = Modifier,
    list: Array<Pair<String, List<String>>> = emptyArray(),
    onSubmit: ((String) -> Unit)? = null,
) {
    Box(modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.TopCenter) {
        if (list.isNotEmpty()) {
            val state = rememberLazyListState()

            LazyColumn(state = state) {
                items(list) { (key, value) ->
                    TextButton(onClick = { onSubmit?.invoke(key) }) {
                        Text(key, modifier = Modifier.weight(1f))
                        Text(value.size.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState = state),
            )
        } else {
            Text("no files found", modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FilePane(
    data: Array<Pair<String, List<String>>>,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    key: String = ""
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(key)
        }
        FileListPane(
            list = data.find { (k, _) -> k == key }?.second?.toTypedArray() ?: emptyArray(),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FileListPane(list: Array<String>, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val state = rememberLazyListState()

        LazyColumn(state = state) {
            items(list) { text ->
                Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = state),
        )
    }
}
