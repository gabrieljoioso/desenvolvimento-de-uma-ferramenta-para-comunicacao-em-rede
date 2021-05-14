
package apsredes.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InformacoesServidor extends Informacoes {
    private static InformacoesServidor instance;
    private final List<InformacoesCliente> clientes;
    private final List<ComandoMensagem> todasAsMensagens;
    private int ultimoIdAtribuido;
    
    private InformacoesServidor() {
        clientes = new ArrayList<>();
        todasAsMensagens = new ArrayList<>();
        ultimoIdAtribuido = 0;
    }
    
    public static synchronized InformacoesServidor getInstance() {
        if (instance == null)
            instance = new InformacoesServidor();
        
        return instance;
    }
    
    public InformacoesCliente addCliente(InformacoesCliente cliente) {
        cliente.setId(++ultimoIdAtribuido);
        clientes.add(cliente);
        return cliente;
    }
    
    public void removeCliente(InformacoesCliente cliente) {
        clientes.removeIf(c -> c.getId() == cliente.getId());
    }
    
    public InformacoesCliente getCliente(int index) {
        if (index > -1 && index < clientes.size())
            return clientes.get(index);
        return null;
    }
    
    public InformacoesCliente getClienteById(int id) {
        if (id > 0) {
            Optional<InformacoesCliente> cliente = clientes.stream()
            .filter(clienteF -> clienteF.getId() == id).findFirst();
            return cliente.isPresent() ? cliente.get() : null;
        }
        return null;
    }
    
    public int getIndexClienteById(int id) {
        if (id > 0) {
            return clientes.indexOf(getClienteById(id));
        }
        return -1;
    }
    
    public int getIndexCliente(InformacoesCliente cliente) {
        if (cliente != null) {
            return clientes.indexOf(cliente);
        }
        return -1;
    }
    
    public void addParaTodasAsMensagens(ComandoMensagem mensagem) {
        todasAsMensagens.add(mensagem);
    }
    
    public List<ComandoMensagem> getTodasAsMensagens() {
        return todasAsMensagens;
    }
    
    public List<ComandoMensagem> getTodasAsMensagensDeParaOuParaDe(int idDe, int idPara) {
        return todasAsMensagens.stream().filter(cmd -> (cmd.getMensagemDe() 
        == idDe && cmd.getMensagemPara() == idPara) || (cmd.getMensagemDe() 
        == idPara && cmd.getMensagemPara() 
        == idDe)).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public List<ComandoMensagem> getTodasAsMensagensPara(int idPara) {
        return todasAsMensagens.stream().filter(cmd -> cmd.getMensagemPara() 
        == idPara).collect(Collectors.toCollection(ArrayList::new));
    }
    
    public void removerDeTodasAsMensagensOndeDeOuParaSeja(int idDeOuPara) {
        todasAsMensagens.removeIf(cmd -> cmd.getMensagemDe() 
        == idDeOuPara || cmd.getMensagemPara() == idDeOuPara);
    }
}
