import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

var memoria = GerenciadorMemoria()
var blocosLivres by mutableStateOf(memoria.blocosLivres)
var todosBlocos by mutableStateOf(memoria.todosBlocos)
var statusMsg by mutableStateOf("")
var estadoVersao by mutableStateOf(0)

data class PassoHistorico(
    val numero: Int,
    val acao: String,
    val resultado: String,
    val ocupado: Int,
    val livre: Int,
    val maiorBrecha: Int,
)

var historico by mutableStateOf(listOf<PassoHistorico>())
private var contadorPasso = 0

fun registrarPasso(acao: String, resultado: String) {
    contadorPasso++
    historico = historico + PassoHistorico(
        numero = contadorPasso,
        acao = acao,
        resultado = resultado,
        ocupado = memoria.totalOcupado(),
        livre = memoria.totalLivre(),
        maiorBrecha = memoria.maiorBrecha(),
    )
}

fun limparHistorico() {
    historico = listOf()
    contadorPasso = 0
}

fun atualizarEstado() {
    blocosLivres = memoria.blocosLivres
    todosBlocos = memoria.todosBlocos
    estadoVersao++
}

enum class Algoritmo(val label: String, val core: GerenciadorMemoria.Algoritmo) {
    FIRST_FIT("Primeira Escolha (First Fit)", GerenciadorMemoria.Algoritmo.FIRST_FIT),
    BEST_FIT("Melhor Escolha (Best Fit)", GerenciadorMemoria.Algoritmo.BEST_FIT),
    WORST_FIT("Pior Escolha (Worst Fit)", GerenciadorMemoria.Algoritmo.WORST_FIT),
}

@Composable
@Preview
fun GerenciadorMemoriaApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                PagSimulador()
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Simulador de Alocação de Memória",
        state = rememberWindowState(width = 900.dp, height = 700.dp),
        onCloseRequest = ::exitApplication
    ) {
        window.minimumSize = Dimension(800, 600)
        GerenciadorMemoriaApp()
    }
}
