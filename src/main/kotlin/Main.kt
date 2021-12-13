// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import java.io.File

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val composeScope = rememberCoroutineScope()

    MaterialTheme {
        Column {
            TextField(value = text, onValueChange = { text = it })
            Button(onClick = {
                composeScope.launch {
                    text = "size: ${buildMap()}"
                }
            }) {
                Text("Run")
            }
        }
    }
}

fun buildMap(): Int {
    val file = File("~/work/github/FolderDemo/src")
    return file.listFiles()?.size ?: 0
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
