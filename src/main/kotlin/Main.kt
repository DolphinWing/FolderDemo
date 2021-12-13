// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("") }
    val composeScope = rememberCoroutineScope()
    var list by remember { mutableStateOf(Array(0) { Pair("", 0) }) }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Project directory")
                    }
                )
                Button(onClick = {
                    composeScope.launch {
                        list = FolderUtils.buildMap(text).toSortedMap()
                            .map { entry -> Pair(entry.key, entry.value) }
                            .toTypedArray()
                    }
                }) {
                    Text("Run")
                }
            }
            if (list.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    items(list) { (key, value) ->
                        Row {
                            Text(key, modifier = Modifier.weight(1f))
                            Text(value.toString(), modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                Text("no files found")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
