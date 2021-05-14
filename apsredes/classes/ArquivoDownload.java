// Classe responsável pelo download do arquivo.
package apsredes.classes;

import apsredes.enums.ComandoEnum;
import java.io.Serializable;

public class ArquivoDownload extends Comandos implements Serializable {
    private byte[] fileBytes;

    public ArquivoDownload(ComandoEnum comando, InformacoesCliente informacoesCliente, byte[] fileBytes) {
        super(comando, informacoesCliente);
        this.fileBytes = fileBytes;
    }
    
    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
    
    public byte[] getFileBytes(){
        return fileBytes;
    }
}
