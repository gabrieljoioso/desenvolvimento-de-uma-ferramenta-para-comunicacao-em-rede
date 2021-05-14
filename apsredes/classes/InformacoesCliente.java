package apsredes.classes;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InformacoesCliente extends Informacoes implements
        Serializable {
    private String ipPublicoCliente;
    private String ipLocalCliente;
    private String nome;
    private int id;
    private ObjectOutputStream out;
    
    public InformacoesCliente(int id, String ipPublicoCliente,
            String ipLocalCliente, String nome) {
        this.ipPublicoCliente = ipPublicoCliente;
        this.ipLocalCliente = ipLocalCliente;
        this.nome = nome;
        this.id = id;
    }
    
    public String getIpPublicoCliente() {
        return ipPublicoCliente;
    }
    
    public void setIpPublicoCliente(String ipPublicoCliente) {
        this.ipPublicoCliente = ipPublicoCliente;
    }
    
    public String getIpLocalCliente() {
        return ipLocalCliente;
    }
    
    public void setIpLocalCliente(String ipLocalCliente) {
        this.ipLocalCliente = ipLocalCliente;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public ObjectOutputStream getOut() {
        return out;
    }
    
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }
}
