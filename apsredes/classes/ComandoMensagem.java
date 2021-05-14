// Comandos de mensagem
package apsredes.classes;

import apsredes.enums.ComandoEnum;

public class ComandoMensagem extends Comandos {
    private int mensagemDe;
    private int mensagemPara;
    private String mensagem;
    
    public ComandoMensagem(ComandoEnum comando, InformacoesCliente informacoesCliente, int mensagemDe, int mensagemPara, String mensagem) {
        super(comando, informacoesCliente);
        this.mensagemDe = mensagemDe;
        this.mensagemPara = mensagemPara;
        this.mensagem = mensagem;
    }
    
    public int getMensagemDe() {
        return mensagemDe;
    }
    
    public int getMensagemPara() {
        return mensagemPara;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
