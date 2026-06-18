public class Bloco {

    private int endereco;
    private int tamanho;
    private boolean disponivel;
    private String idProcesso; 
    private Bloco proximo;

    public Bloco(int endereco, int tamanho) {
        this(endereco, tamanho, true);
    }

    public Bloco(int endereco, int tamanho, boolean disponivel) {
        this.endereco = endereco;
        this.tamanho = tamanho;
        this.disponivel = disponivel;
        this.idProcesso = null;
        this.proximo = null;
    }

    public Bloco(int endereco, int tamanho, boolean disponivel, String idProcesso) {
        this.endereco = endereco;
        this.tamanho = tamanho;
        this.disponivel = disponivel;
        this.idProcesso = idProcesso;
        this.proximo = null;
    }

    public void diminuir(int tamanho) {
        this.tamanho -= tamanho;
    }

    public void aumentar(int tamanho) {
        this.tamanho += tamanho;
    }

    public int getEndereco() {
        return endereco;
    }

    public void setEndereco(int endereco) {
        this.endereco = endereco;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(String idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Bloco getProximo() {
        return proximo;
    }

    public void setProximo(Bloco proximo) {
        this.proximo = proximo;
    }

    @Override
    public String toString() {
        if (disponivel) {
            return "[" + endereco + "|" + tamanho + "|livre]";
        }
        return "[" + endereco + "|" + tamanho + "|" + idProcesso + "]";
    }
}
