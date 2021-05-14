package apsredes.classes;

import apsredes.enums.ComandoEnum;
import apsredes.ui.UICliente;
import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class SocketCliente {
    private final Socket socket;
    private final InformacoesCliente informacoesCliente;
    private final UICliente clienteScreen;
    private final List<InformacoesCliente> listaClientes;
    private final ObjectOutputStream out;
    private ObjectInputStream in;
    
    public SocketCliente(InformacoesCliente infoCliente, 
        UICliente clienteScreen, Socket socket) throws IOException {
        this.informacoesCliente = infoCliente;
        this.clienteScreen = clienteScreen;
        this.listaClientes = new ArrayList<>();
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        registrarCliente();
        comecarAOuvir();
    }
    
    private void registrarCliente() {
        enviarMensagem(ComandoEnum.REGISTRAR);
    }
    
    private void comecarAOuvir() {
        Runnable runnableListen = () -> {
            try {
                this.in = new ObjectInputStream(this.socket.getInputStream());
                Comandos comandoObj;
                while ((comandoObj = (Comandos) in.readObject()) != null) {
                    if (comandoObj.getComando() != null) {
                        switch (comandoObj.getComando()) {
                            case ADICIONAR:
                                listaClientes.add(comandoObj.getInformacoesCliente());
                                clienteScreen.adicionarClienteAListaClientes
                                (comandoObj.getInformacoesCliente());
                                break;
                            case REMOVER:
                                listaClientes.remove(comandoObj.getInformacoesCliente());
                                clienteScreen.removerClienteDaListaClientes
                                (comandoObj.getInformacoesCliente());
                                break;
                            case DESCONECTAR:
                                clienteScreen.desconectarAMim();
                                break;
                            case REGISTRAR:
                                informacoesCliente.setId(comandoObj
                                        .getInformacoesCliente().getId());
                                clienteScreen.registrarAMim(informacoesCliente);
                                break;
                            case MENSAGEM:
                            case MENSAGEMA:
                                if (comandoObj instanceof ComandoMensagem) {
                                    ComandoMensagem comandoMensagemObj 
                                            = ((ComandoMensagem) comandoObj);
                                    if (comandoObj.getComando() == ComandoEnum.MENSAGEM)
                                        clienteScreen.adicionarMensagemNoCampoDeMensagens
                                        (comandoMensagemObj);
                                }
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException error) {
                System.out.println(error.getMessage());
            }
        };
        Thread threadListen = new Thread(runnableListen);
        threadListen.start();
    }
    
    public void enviarMensagem(ComandoEnum comando) {
        try {
            Comandos mensagemObj = new Comandos(comando, informacoesCliente);
            out.writeObject(mensagemObj);
            out.flush();
        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, error.getMessage(), 
                "Erro ao enviar mensagem.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void enviarMensagem(ComandoEnum comando, 
           int mensagemDe, int mensagemPara, String mensagem) {
        try {
            Comandos mensagemObj = new ComandoMensagem
        (comando, informacoesCliente, mensagemDe, mensagemPara, mensagem + "\n");
            out.writeObject(mensagemObj);
            out.flush();
            
        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, error.getMessage(), 
                    "Erro ao enviar mensagem.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void transferirArquivo(int idSelecionadoDaListaClientes, File file) {
        try {
            byte[] ipLocalByteArr = InetAddress.getByName
        (informacoesCliente.getIpLocalServidor()).getAddress();
            Socket socketTransferirArquivo = new Socket(InetAddress.getByAddress
        (informacoesCliente.getIpPublicoServidor(), ipLocalByteArr), 
                    informacoesCliente.getPortFileUpload());
            byte[] fileBytes = new byte[(int)file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = 
                    new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
            ArquivoUpload fileUpload = new ArquivoUpload
            (ComandoEnum.ARQUIVOUPLOAD, informacoesCliente, fileBytes, 
               file.getName(), idSelecionadoDaListaClientes);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream
                (socketTransferirArquivo.getOutputStream());
            objectOutputStream.writeObject(fileUpload);
            objectOutputStream.flush();
            out.writeObject(fileUpload);
            out.flush();
        } catch (FileNotFoundException error) {
            JOptionPane.showMessageDialog(null, error.getMessage(), 
                "Arquivo não encontrado.", JOptionPane.ERROR_MESSAGE);
        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, error.getMessage(), 
                "IOException", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void baixarArquivo(String caminhoArquivoServidor, 
            String localParaSalvar) { // metodo para baixar arquivo
        String arquivoSemDiretorio = caminhoArquivoServidor.replace
        (informacoesCliente.getDiretorioArquivos(), "");
        Pattern extrairApenasONomeDoArquivo = Pattern.compile("_.{1,}$");
        Matcher matcher = extrairApenasONomeDoArquivo.matcher
        (arquivoSemDiretorio);
        String nomeArquivo = "";
        if (matcher.find())
            nomeArquivo = matcher.group();
        nomeArquivo = nomeArquivo.substring(1);
        try {
            byte[] ipLocalByteArr = InetAddress.getByName
            (informacoesCliente.getIpLocalServidor()).getAddress();
            Socket socketDownloadArquivo = new Socket(InetAddress.getByAddress
            (informacoesCliente.getIpPublicoServidor(), ipLocalByteArr), 
            informacoesCliente.getPortFileDownload());
            PrintWriter printWriter = new PrintWriter
            (socketDownloadArquivo.getOutputStream());
            printWriter.println(caminhoArquivoServidor);
            printWriter.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream
            (socketDownloadArquivo.getInputStream());
            ArquivoDownload fileDownload = 
            (ArquivoDownload) objectInputStream.readObject();
            if (!new File(localParaSalvar).exists()) {
                new File(localParaSalvar).mkdirs();
            }
            nomeArquivo = localParaSalvar + "\\" + nomeArquivo;
            new File(nomeArquivo).createNewFile();
            try (OutputStream outputStream = new FileOutputStream(nomeArquivo)) {
                outputStream.write(fileDownload.getFileBytes());
            }
            JOptionPane.showMessageDialog(null, "O arquivo foi baixado com sucesso! " 
            + nomeArquivo, "Sucesso ao baixar arquivo.", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | IOException | ClassNotFoundException error) {
            JOptionPane.showMessageDialog(null, error.getMessage(), 
                "Erro ao baixar arquivo.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<InformacoesCliente> getListaClientes() {
        return this.listaClientes;
    }
    
    public InformacoesCliente getClienteDaListaClientes(int index) {
        if (index > -1 && index < this.listaClientes.size())
            return this.listaClientes.get(index);
        return null;
    }
}
