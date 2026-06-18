import java.io.File

data class ResultadoLeitura(
    val sucesso: Boolean,
    val mensagem: String,
)

fun carregarArquivoDeEventos(arquivo: File): ResultadoLeitura {
    if (!arquivo.exists()) {
        return ResultadoLeitura(false, "Arquivo nao encontrado: ${arquivo.path}")
    }

    var memoriaDefinida = false
    var passo = 0
    var alocacoes = 0
    var liberacoes = 0
    var falhas = 0

    try {
        arquivo.forEachLine { bruta ->
            val linha = bruta.trim()
            if (linha.isEmpty() || linha.startsWith("#")) return@forEachLine

            val t = linha.split(Regex("\\s+"))
            when (t[0].uppercase()) {
                "MEMORIA" -> {
                    val tamanho = t[1].toInt()
                    val alg = if (t.size >= 3) parseAlgoritmoArquivo(t[2]) else Algoritmo.FIRST_FIT
                    memoria = GerenciadorMemoria(tamanho)
                    memoria.algoritmo = alg.core
                    memoriaDefinida = true
                    limparHistorico()
                    registrarPasso("Definir memoria ($tamanho bytes, ${alg.label})", "Simulacao iniciada pelo arquivo.")
                }

                "ALOCAR" -> {
                    if (!memoriaDefinida) {
                        throw IllegalStateException("Defina MEMORIA antes de ALOCAR.")
                    }
                    val id = t[1]
                    val tam = t[2].toInt()
                    passo++
                    val haFrag = memoria.haFragmentacaoExterna(tam)
                    val endereco = memoria.alocar(id, tam)
                    val res = when {
                        endereco >= 0 -> "OK: alocado no endereco $endereco."
                        haFrag -> "FALHA (fragmentacao externa): maior brecha ${memoria.maiorBrecha()} B."
                        else -> "FALHA: espaco insuficiente (${memoria.totalLivre()} B livres)."
                    }
                    if (endereco >= 0) alocacoes++ else falhas++
                    registrarPasso("Alocar $id ($tam bytes)", res)
                }

                "LIBERAR" -> {
                    if (!memoriaDefinida) {
                        throw IllegalStateException("Defina MEMORIA antes de LIBERAR.")
                    }
                    val id = t[1]
                    passo++
                    val ok = memoria.liberar(id)
                    if (ok) liberacoes++
                    registrarPasso(
                        "Liberar $id",
                        if (ok) "OK: memoria liberada." else "Aviso: processo nao encontrado."
                    )
                }
            }
        }
    } catch (e: NumberFormatException) {
        return ResultadoLeitura(false, "Erro de formato no arquivo: numero invalido (${e.message}).")
    } catch (e: IndexOutOfBoundsException) {
        return ResultadoLeitura(false, "Erro de formato no arquivo: linha incompleta.")
    } catch (e: IllegalStateException) {
        return ResultadoLeitura(false, e.message ?: "Erro ao processar o arquivo.")
    } catch (e: Exception) {
        return ResultadoLeitura(false, "Erro ao ler o arquivo: ${e.message}")
    }

    if (!memoriaDefinida) {
        return ResultadoLeitura(false, "Arquivo sem comando MEMORIA: nada foi executado.")
    }

    atualizarEstado()

    val resumo = "Arquivo carregado: $passo evento(s) — " +
            "$alocacoes alocacao(oes), $liberacoes liberacao(oes), $falhas falha(s)."
    return ResultadoLeitura(true, resumo)
}

private fun parseAlgoritmoArquivo(s: String): Algoritmo =
    runCatching { Algoritmo.valueOf(s.uppercase()) }.getOrDefault(Algoritmo.FIRST_FIT)
