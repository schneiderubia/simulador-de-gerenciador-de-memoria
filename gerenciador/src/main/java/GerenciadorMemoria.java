import java.util.ArrayList;
import java.util.List;

public class GerenciadorMemoria {

    public enum Algoritmo {
        FIRST_FIT, BEST_FIT, WORST_FIT
    }

    private Bloco inicio;
    private final int tamanhoTotal;
    private Algoritmo algoritmo = Algoritmo.FIRST_FIT;
    private int alocacoesComSucesso = 0;
    private int falhasPorEspaco = 0;      
    private int falhasPorFragmentacao = 0;  

    public GerenciadorMemoria() {
        this(4096);
    }

    public GerenciadorMemoria(int tamanho) {
        this.tamanhoTotal = tamanho;
        this.inicio = new Bloco(0, tamanho, true);
    }

    public void setAlgoritmo(Algoritmo algoritmo) {
        this.algoritmo = algoritmo;
    }

    public Algoritmo getAlgoritmo() {
        return algoritmo;
    }

    public int alocar(String idProcesso, int tamanho) {
        if (tamanho <= 0) {
            return -1;
        }

        Bloco escolhido = selecionarBrecha(tamanho);

        if (escolhido == null) {
            if (totalLivre() >= tamanho) {
                falhasPorFragmentacao++;
            } else {
                falhasPorEspaco++;
            }
            return -1;
        }

        int enderecoAlocado = escolhido.getEndereco();

        if (escolhido.getTamanho() == tamanho) {
            escolhido.setDisponivel(false);
            escolhido.setIdProcesso(idProcesso);
        } else {
            Bloco ocupado = new Bloco(enderecoAlocado, tamanho, false, idProcesso);
            escolhido.setEndereco(enderecoAlocado + tamanho);
            escolhido.setTamanho(escolhido.getTamanho() - tamanho);
            inserirAntes(escolhido, ocupado);
        }

        alocacoesComSucesso++;
        return enderecoAlocado;
    }

    private Bloco selecionarBrecha(int tamanho) {
        switch (algoritmo) {
            case BEST_FIT:
                return melhorEscolha(tamanho);
            case WORST_FIT:
                return piorEscolha(tamanho);
            case FIRST_FIT:
            default:
                return primeiraEscolha(tamanho);
        }
    }

    private Bloco primeiraEscolha(int tamanho) {
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.isDisponivel() && atual.getTamanho() >= tamanho) {
                return atual;
            }
            atual = atual.getProximo();
        }
        return null;
    }

    private Bloco melhorEscolha(int tamanho) {
        Bloco melhor = null;
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.isDisponivel() && atual.getTamanho() >= tamanho) {
                if (melhor == null || atual.getTamanho() < melhor.getTamanho()) {
                    melhor = atual;
                }
            }
            atual = atual.getProximo();
        }
        return melhor;
    }

    private Bloco piorEscolha(int tamanho) {
        Bloco pior = null;
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.isDisponivel() && atual.getTamanho() >= tamanho) {
                if (pior == null || atual.getTamanho() > pior.getTamanho()) {
                    pior = atual;
                }
            }
            atual = atual.getProximo();
        }
        return pior;
    }

    private void inserirAntes(Bloco alvo, Bloco novo) {
        if (inicio == alvo) {
            novo.setProximo(alvo);
            inicio = novo;
            return;
        }
        Bloco ant = inicio;
        while (ant != null && ant.getProximo() != alvo) {
            ant = ant.getProximo();
        }
        if (ant != null) {
            novo.setProximo(alvo);
            ant.setProximo(novo);
        }
    }

    public boolean liberar(String idProcesso) {
        boolean liberouAlgo = false;
        Bloco atual = inicio;
        while (atual != null) {
            if (!atual.isDisponivel() && idProcesso.equals(atual.getIdProcesso())) {
                atual.setDisponivel(true);
                atual.setIdProcesso(null);
                liberouAlgo = true;
            }
            atual = atual.getProximo();
        }
        if (liberouAlgo) {
            coalescer();
        }
        return liberouAlgo;
    }

    public void liberar(int endereco, int tamanho) {
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.getEndereco() == endereco && !atual.isDisponivel()) {
                atual.setDisponivel(true);
                atual.setIdProcesso(null);
                break;
            }
            atual = atual.getProximo();
        }
        coalescer();
    }

    private void coalescer() {
        Bloco atual = inicio;
        while (atual != null && atual.getProximo() != null) {
            Bloco prox = atual.getProximo();
            if (atual.isDisponivel() && prox.isDisponivel()) {
                atual.setTamanho(atual.getTamanho() + prox.getTamanho());
                atual.setProximo(prox.getProximo());
            } else {
                atual = atual.getProximo();
            }
        }
    }

    public List<Bloco> getBlocosLivres() {
        List<Bloco> lista = new ArrayList<Bloco>();
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.isDisponivel()) {
                lista.add(atual);
            }
            atual = atual.getProximo();
        }
        return lista;
    }

    public List<Bloco> getTodosBlocos() {
        List<Bloco> lista = new ArrayList<Bloco>();
        Bloco atual = inicio;
        while (atual != null) {
            lista.add(atual);
            atual = atual.getProximo();
        }
        return lista;
    }

    public int totalLivre() {
        int total = 0;
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.isDisponivel()) {
                total += atual.getTamanho();
            }
            atual = atual.getProximo();
        }
        return total;
    }

    public int totalOcupado() {
        return tamanhoTotal - totalLivre();
    }

    public int maiorBrecha() {
        int maior = 0;
        Bloco atual = inicio;
        while (atual != null) {
            if (atual.isDisponivel() && atual.getTamanho() > maior) {
                maior = atual.getTamanho();
            }
            atual = atual.getProximo();
        }
        return maior;
    }

    public boolean haFragmentacaoExterna(int tamanho) {
        return totalLivre() >= tamanho && maiorBrecha() < tamanho;
    }

    public void imprimir() {
        Bloco atual = inicio;
        while (atual != null) {
            System.out.print(atual + "->");
            atual = atual.getProximo();
        }
        System.out.println();
    }

    public void imprimirEstado() {
        System.out.println("  Memória [total=" + tamanhoTotal + ", algoritmo=" + algoritmo + "]");
        for (Bloco b : getTodosBlocos()) {
            String faixa = "    " + b.getEndereco() + " .. " + (b.getEndereco() + b.getTamanho() - 1)
                    + " (" + b.getTamanho() + ")";
            if (b.isDisponivel()) {
                System.out.println(faixa + "  [BRECHA livre]");
            } else {
                System.out.println(faixa + "  [processo " + b.getIdProcesso() + "]");
            }
        }
    }

    public int getAlocacoesComSucesso() {
        return alocacoesComSucesso;
    }

    public int getFalhasPorEspaco() {
        return falhasPorEspaco;
    }

    public int getFalhasPorFragmentacao() {
        return falhasPorFragmentacao;
    }

    public int getTotalFalhas() {
        return falhasPorEspaco + falhasPorFragmentacao;
    }

    public double utilizacaoPercentual() {
        return tamanhoTotal == 0 ? 0.0 : (100.0 * totalOcupado() / tamanhoTotal);
    }

    public int getTamanhoTotal() {
        return tamanhoTotal;
    }
}