//Comandos e informações do cliente
package apsredes.classes;

import apsredes.enums.ComandoEnum;
import java.io.Serializable;

public class Comandos implements Serializable {
    private ComandoEnum comando;
    private InformacoesCliente informacoesCliente;
    
    public Comandos(ComandoEnum comando, InformacoesCliente informacoesCliente) {
        this.comando = comando;
        this.informacoesCliente = informacoesCliente;
        if (informacoesCliente != null)
            this.informacoesCliente.setOut(null);
    }
    
    public ComandoEnum getComando() {
        return comando;
    }
    
    public InformacoesCliente getInformacoesCliente() {
        return informacoesCliente;
    }
}
