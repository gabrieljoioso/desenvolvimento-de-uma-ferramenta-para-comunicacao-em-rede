// Classe responsavel pelo upload do arquivo
package apsredes.classes;

import apsredes.enums.ComandoEnum;
import java.io.Serializable;

public class ArquivoUpload extends ArquivoDownload implements Serializable {
    private final String nome;
    private final long currentTimeMillis;
    private int arquivoPara;
    
    public ArquivoUpload(ComandoEnum comando, InformacoesCliente informacoesCliente, byte[] fileBytes, String nome, int arquivoPara) {
        super(comando, informacoesCliente, fileBytes);
        this.nome = nome;
        this.currentTimeMillis = System.currentTimeMillis();
        this.arquivoPara = arquivoPara;
    }
    
    public String getNome() {
        return nome;
    }
    
    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }
    
    public int getArquivoPara() {
        return arquivoPara;
    }
}
