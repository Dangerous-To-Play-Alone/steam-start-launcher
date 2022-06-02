// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import api.Application
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.*
import java.security.MessageDigest


@ExperimentalFoundationApi
@Composable
@Preview
fun App(
    windowState: WindowState,
    onExit: () -> Unit
) {

    val trayState = rememberTrayState()
    val notification = rememberNotification("Notification", "Message from MyApp!")

    val coroutineScope = rememberCoroutineScope()

    Tray(
        state = trayState,
        icon = painterResource("logo.svg"),
        menu = {
            Item(
                "Populate Start Folder",
                onClick = {
                    PopulateGamesUseCase().invoke(coroutineScope)
                }
            )
            Item(
                "Open",
                onClick = {
                    windowState.isMinimized = false
                }
            )
            Item(
                "Close",
                onClick = onExit
            )
        }
    )
    DesktopMaterialTheme {

        val ownedGames by GameRepository().fetchOwnedGames().collectAsState(null)

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        PopulateGamesUseCase().invoke(coroutineScope)
                    }
                ) {
                    Text("Save All")
                }

                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        Desktop.getDesktop().open(
                            File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Steam Games")
                        )
                    }
                ) {
                    Text("Open Shortcuts Folder")
                }
            }

            ownedGames?.let { games ->
                LazyVerticalGrid(
                    cells = GridCells.Adaptive(256.dp)
                ) {
                    items(games) { game ->
                        Card(
                            modifier = Modifier.height(128.dp)
                                .padding(8.dp),
                            elevation = 3.dp
                        ) {
                            KamelImage(
                                resource = lazyPainterResource("https://steamcdn-a.akamaihd.net/steam/apps/${game.appid}/header.jpg"),
                                contentDescription = null,
                                onLoading = {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        CircularProgressIndicator()
                                    }
                                },
                                onFailure = { exception ->
                                    Snackbar {
                                        Text(exception.message!!)
                                    }
                                }
                            )
                        }
                    }
                }
            }

        }
    }

}

@ExperimentalFoundationApi
fun main() = application {

    val windowState = rememberWindowState()

    Window(
        state = windowState,
        title = "Steam Start Launcher",
        onCloseRequest = {
            windowState.isMinimized = true
        },
        icon = painterResource("logo.svg"),
    ) {
        App(
            windowState = windowState,
            onExit = ::exitApplication
        )
    }
}

object MyAppIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color.Green, Offset(size.width / 4, 0f), Size(size.width / 2f, size.height))
        drawOval(Color.Blue, Offset(0f, size.height / 4), Size(size.width, size.height / 2f))
        drawOval(
            Color.Red,
            Offset(size.width / 4, size.height / 4),
            Size(size.width / 2f, size.height / 2f)
        )
    }
}

fun Application.save(
    scope: CoroutineScope
) {
    val app = this
    scope.launch(Dispatchers.IO) {
        val url = "steam://rungameid/${app.appid}"

        val file = File(
            "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Steam Games\\",
            "${app.name.replace(":", "")}.URL"
        )
        val fw = FileWriter(file)

//        GameService().fetchGameIcon(appid)
//            .collect {
                try {
                    fw.write("[InternetShortcut]\n")
                    fw.write("URL=$url\n")
                    fw.write("IconIndex=0\n")
                    fw.write("IconFile=https://cdn.cloudflare.steamstatic.com/steam/apps/${appid}/hero_capsule.jpg")
//                    fw.write(it)
                } catch (e: Exception) {
                    e.printStackTrace()
//                }
            }


        fw.flush()
        fw.close()

        delay(200)
    }
}

val String.sha1: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }