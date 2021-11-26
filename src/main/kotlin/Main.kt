// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import VDF.VDF
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.Application
import api.GameService
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.*
import java.net.URL
import javax.imageio.ImageIO


@ExperimentalFoundationApi
@Composable
@Preview
fun App() {
    DesktopMaterialTheme {

        val games by GameService().getGames().collectAsState(null)
        val coroutineScope = rememberCoroutineScope()

        val vdf = VDF(File("C:\\Program Files (x86)\\Steam\\steamapps\\libraryfolders.vdf"))
        val gameIds = vdf.getParent("libraryfolders")?.parents?.map {
            it?.getParent("apps")?.keys
        }?.fold(emptyList<String?>()) { list, items -> list + items!! }

        games?.let {
            val ownedGames = it.applist.apps.filter { gameIds?.contains(it.appid.toString()) ?: false }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Button(
                        modifier = Modifier.padding(4.dp),
                        onClick = {
                            File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Steam Games").apply {
                                this.listFiles()?.forEach { it.delete() }
                            }
                            ownedGames.forEach { it.save(coroutineScope) }
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

                LazyVerticalGrid(
                    cells = GridCells.Adaptive(256.dp)
                ) {
                    items(ownedGames) { game ->
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
        } ?: run {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

    }
}

@ExperimentalFoundationApi
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

fun Application.save(
    scope: CoroutineScope
) {
    val app = this
    scope.launch(Dispatchers.IO) {
        val url = "steam://run/${app.appid}"

        val file = File("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Steam Games\\", "${app.name}.URL")
        val fw = FileWriter(file)
        fw.write("[InternetShortcut]\n")
        fw.write("URL=$url\n")
        fw.write("IconIndex=0\n")
        fw.write("IconFile=https://steamcdn-a.akamaihd.net/steam/apps/${appid}/library_600x900.jpg\n")


        fw.flush()
        fw.close()

        delay(200)
    }
}