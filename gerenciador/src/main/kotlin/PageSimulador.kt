import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PagSimulador() {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f)) { CardTamanhoMemoria() }
            Spacer(Modifier.padding(8.dp))
            Box(Modifier.weight(1f)) { CardCarregarArquivo() }
        }
        Spacer(Modifier.padding(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f)) { CardAlocarMemoria() }
            Spacer(Modifier.padding(8.dp))
            Box(Modifier.weight(1f)) { CardLiberarMemoria() }
        }
        Spacer(Modifier.padding(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            CardSimuladorMemoria()
        }
        Spacer(Modifier.padding(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            CardHistorico()
        }
    }
}

@Composable
fun CardTamanhoMemoria() {
    var tamanho by remember { mutableStateOf(TextFieldValue("")) }
    Card {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "Tamanho da memoria",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tamanho da memoria", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Straighten, contentDescription = "Tamanho")
                },
                trailingIcon = { Text("Byte(s)", modifier = Modifier.padding(10.dp)) },
                value = tamanho,
                onValueChange = { tamanho = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Tamanho") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val novoTamanho = tamanho.text.toIntOrNull()
                    if (novoTamanho != null && novoTamanho > 0) {
                        memoria = GerenciadorMemoria(novoTamanho)
                        limparHistorico()
                        statusMsg = "Memória redefinida para $novoTamanho bytes."
                        registrarPasso("Redefinir memoria ($novoTamanho bytes)", "Simulação reiniciada.")
                        atualizarEstado()
                    }
                }
            ) {
                Text("Definir / Redefinir")
            }
        }
    }
}

@Composable
fun CardHistorico() {
    @Suppress("UNUSED_EXPRESSION") estadoVersao
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Historico",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Histórico de passos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (historico.isEmpty()) {
                Text(
                    text = "Nenhum passo ainda. Aloque, libere ou carregue um arquivo.",
                    fontSize = 13.sp
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        historico.asReversed().forEach { passo ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(
                                    text = "Passo ${passo.numero}: ${passo.acao}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = passo.resultado, fontSize = 12.sp)
                                Text(
                                    text = "Estado: ocupado ${passo.ocupado} B | livre ${passo.livre} B | " +
                                            "maior brecha ${passo.maiorBrecha} B",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                HorizontalDivider(Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardCarregarArquivo() {
    var resumo by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    Card {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = "Carregar arquivo",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Carregar arquivo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Arquivo .txt",
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val arquivo = selecionarArquivo()
                    if (arquivo != null) {
                        val r = carregarArquivoDeEventos(arquivo)
                        erro = !r.sucesso
                        resumo = r.mensagem
                        statusMsg = r.mensagem
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Escolher arquivo...")
            }
            if (resumo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resumo,
                    fontSize = 12.sp,
                    color = if (erro) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun selecionarArquivo(): java.io.File? {
    val dialog = java.awt.FileDialog(null as java.awt.Frame?, "Selecione o arquivo de eventos", java.awt.FileDialog.LOAD)
    dialog.isVisible = true
    val nome = dialog.file ?: return null
    return java.io.File(dialog.directory, nome)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardAlocarMemoria() {
    var idProcesso by remember { mutableStateOf(TextFieldValue("")) }
    var tamanho by remember { mutableStateOf(TextFieldValue("")) }
    var algoritmoSelecionado by remember { mutableStateOf(Algoritmo.FIRST_FIT) }
    Card {
        Column(Modifier.padding(12.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Alocar memória",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Alocar memória", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Column(Modifier.fillMaxWidth()) {
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        OutlinedTextField(
                            value = algoritmoSelecionado.label,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Swipe,
                                    contentDescription = "Algoritmo"
                                )
                            },
                            label = { Text(text = "Algoritmo de seleção") },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            Algoritmo.entries.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item.label) },
                                    onClick = {
                                        algoritmoSelecionado = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = "ID do processo"
                        )
                    },
                    value = idProcesso,
                    onValueChange = { idProcesso = it },
                    label = { Text(text = "ID do processo") },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = "Alocar memória"
                        )
                    },
                    trailingIcon = { Text("Byte(s)", modifier = Modifier.padding(10.dp)) },
                    value = tamanho,
                    onValueChange = { tamanho = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Tamanho") },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val tam = tamanho.text.toIntOrNull()
                    val id = idProcesso.text.trim()
                    if (id.isNotEmpty() && tam != null && tam > 0) {
                        memoria.algoritmo = algoritmoSelecionado.core
                        val haFrag = memoria.haFragmentacaoExterna(tam)
                        val endereco = memoria.alocar(id, tam)
                        statusMsg = when {
                            endereco >= 0 ->
                                "OK: processo $id alocado no endereço $endereco (${algoritmoSelecionado.label})."
                            haFrag ->
                                "FALHA (fragmentação externa): há ${memoria.totalLivre()} bytes livres no total, " +
                                        "mas a maior brecha contígua é de apenas ${memoria.maiorBrecha()} bytes."
                            else ->
                                "FALHA: espaço insuficiente — apenas ${memoria.totalLivre()} bytes livres."
                        }
                        registrarPasso("Alocar $id ($tam bytes)", statusMsg)
                        memoria.imprimir()
                        atualizarEstado()
                        idProcesso = TextFieldValue()
                        tamanho = TextFieldValue()
                    }
                }
            ) {
                Text("Alocar")
            }
        }
    }
}

@Composable
fun CardLiberarMemoria() {
    var idProcesso by remember { mutableStateOf(TextFieldValue("")) }
    Card {
        Column(Modifier.padding(12.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Liberar memória",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Liberar memória", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.Tag, contentDescription = "ID do processo") },
                    value = idProcesso,
                    onValueChange = { idProcesso = it },
                    label = { Text(text = "ID do processo") },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val id = idProcesso.text.trim()
                    if (id.isNotEmpty()) {
                        val ok = memoria.liberar(id)
                        statusMsg = if (ok)
                            "OK: memória do processo $id liberada."
                        else
                            "Aviso: nenhum bloco do processo $id foi encontrado."
                        registrarPasso("Liberar $id", statusMsg)
                        memoria.imprimir()
                        atualizarEstado()
                        idProcesso = TextFieldValue()
                    }
                }
            ) {
                Text("Liberar")
            }
        }
    }
}

@Composable
fun CardSimuladorMemoria() {
    Card {
        @Suppress("UNUSED_EXPRESSION") estadoVersao
        Column(Modifier.padding(top = 8.dp)) {
            if (statusMsg.isNotEmpty()) {
                Text(
                    text = statusMsg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
            Text(
                text = "Brechas livres (endereço | tamanho):",
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            View1()
            Text(
                text = "Mapa da memória (cinza = ocupado, verde = livre):",
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            View2()
            ViewEstatisticas()
        }
    }
}

@Composable
fun ViewEstatisticas() {
    @Suppress("UNUSED_EXPRESSION") estadoVersao
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Text("Estatísticas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        val util = memoria.utilizacaoPercentual()
        Text(
            "Utilização: %.2f%%  (%d / %d bytes)".format(util, memoria.totalOcupado(), memoria.tamanhoTotal),
            fontSize = 12.sp
        )
        Text("Maior brecha contígua livre: ${memoria.maiorBrecha()} bytes", fontSize = 12.sp)
        Text("Alocações bem-sucedidas: ${memoria.alocacoesComSucesso}", fontSize = 12.sp)
        Text(
            "Processos não alocados: ${memoria.totalFalhas} " +
                    "(espaço: ${memoria.falhasPorEspaco}, fragmentação: ${memoria.falhasPorFragmentacao})",
            fontSize = 12.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun View1() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        blocosLivres.forEachIndexed { index, bloco ->
            Chip(
                modifier = Modifier.weight(1f).height(36.dp),
                border = BorderStroke(1.dp, Color.Black),
                shape = RectangleShape,
                onClick = {}
            ) {
                Text(
                    text = bloco.endereco.toString().plus(" | ").plus(bloco.tamanho.toString()),
                    softWrap = false,
                    fontSize = 12.sp
                )
            }
            if (index < blocosLivres.size - 1) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward"
                )
            }
        }
    }
}

@Composable
fun View2() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        todosBlocos.forEach {
            Column(modifier = Modifier.weight(it.tamanho.toFloat()).fillMaxWidth()) {
                Row {
                    Text(
                        if (it.tamanho >= memoria.tamanhoTotal * 0.05f) it.endereco.toString() else "",
                        fontSize = 10.sp,
                        softWrap = false,
                        modifier = Modifier.weight(1f)
                    )
                    if (it.endereco + it.tamanho == memoria.tamanhoTotal) {
                        Text(memoria.tamanhoTotal.toString(), softWrap = false, fontSize = 10.sp)
                    }
                }
                Box(
                    modifier = Modifier.background(
                        if (it.isDisponivel) Color.Green else Color.Gray
                    ).fillMaxWidth().height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val rotulo = if (it.isDisponivel) "" else (it.idProcesso ?: "")
                    if (rotulo.isNotEmpty() && it.tamanho >= memoria.tamanhoTotal * 0.05f) {
                        Text(rotulo, fontSize = 10.sp, color = Color.White, softWrap = false)
                    }
                }
            }
        }
    }
}