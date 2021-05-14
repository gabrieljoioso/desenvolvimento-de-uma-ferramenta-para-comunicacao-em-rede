package apsredes.classes;

import apsredes.enums.ComandoEnum;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SocketsServidores {
    private InformacoesServidor informacoesServidor;
    private boolean servidoresContinuemOuvindo;
    private ServerSocket mensagensSocket;
    private ServerSocket fileUploadSocket;
    private ServerSocket fileDownloadSocket;
    private Runnable runnableMensagemSocket;
    private Runnable runnableFileUploadSocket;
    private Runnable runnableFileDownloadSocket;
    private Thread threadMensagemSocket;
    private Thread threadFileUploadSocket;
    private Thread threadFileDownloadSocket;
    public static List<ObjectOutputStream> outEuEOutrosClientes;
    
    public SocketsServidores() {
        this.informacoesServidor = InformacoesServidor.getInstance();
        try {
            byte[] ipLocalByteArr = InetAddress.getByName(informacoesServidor.getIpLocalServidor()).getAddress();
            this.mensagensSocket = new ServerSocket(informacoesServidor.getPortServidor(), 1, 
                    InetAddress.getByAddress(informacoesServidor.getIpPublicoServidor(), ipLocalByteArr));
            this.fileUploadSocket = new ServerSocket(informacoesServidor.getPortFileUpload(), 1, 
                    InetAddress.getByAddress(informacoesServidor.getIpPublicoServidor(), ipLocalByteArr));
            this.fileDownloadSocket = new ServerSocket(informacoesServidor.getPortFileDownload(), 1, 
                    InetAddress.getByAddress(informacoesServidor.getIpPublicoServidor(), ipLocalByteArr));
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }
    
    public void servidoresComecemAOuvir() {
        servidoresContinuemOuvindo = true;
        mensagemSocketComeceAOuvir();
        fileUploadSocketComeceAOuvir();
        fileDownloadSocketComeceAOuvir();
    }
    // log do servidor
    private void mensagemSocketComeceAOuvir() {
        this.runnableMensagemSocket = () -> {
            try {
                outEuEOutrosClientes = new ArrayList<>();
                while(servidoresContinuemOuvindo) {
                    Socket socketClient = this.mensagensSocket.accept();
                    ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
                    Comandos comandoObj = (Comandos) in.readObject();
                    ObjectOutputStream out = new ObjectOutputStream(socketClient.getOutputStream());
                    InformacoesCliente informacoesEuCliente = this.registrarCliente(comandoObj);
                    informacoesEuCliente.setOut(out);
                    System.out.println("Nova conexão: " + informacoesEuCliente.getIpPublicoCliente() + "/" + informacoesEuCliente.getIpLocalCliente());
                    Iterator<ObjectOutputStream> iterador = outEuEOutrosClientes.iterator();
                    for (int i = 0; iterador.hasNext(); i++) {
                        ObjectOutputStream outOutroCliente = iterador.next();
                        outOutroCliente.writeObject(new Comandos(ComandoEnum.ADICIONAR, informacoesEuCliente));
                        outOutroCliente.flush();

                        InformacoesCliente esteOutroCliente = this.informacoesServidor.getCliente(i);
                        if (esteOutroCliente != null) {
                            out.writeObject(new Comandos(ComandoEnum.ADICIONAR, esteOutroCliente));
                            out.flush();
                        }
                    }
                    outEuEOutrosClientes.add(out);
                    out.writeObject(new Comandos(ComandoEnum.REGISTRAR, informacoesEuCliente));
                    out.flush();
                    
                    List<ComandoMensagem> todasAsMensagens = informacoesServidor.getTodasAsMensagensPara(0);
                    for (int i = 0, l = todasAsMensagens.size(); i < l; i++) {
                        ComandoMensagem mensagemObj = todasAsMensagens.get(i);
                        out.writeObject(mensagemObj);
                        out.flush();
                    }
                    new ThreadMensagens(socketClient, informacoesEuCliente, this, in, out).start();
                }
            } catch (IOException | ClassNotFoundException error) {
                System.out.println(error.getMessage());
            }
        };
        this.threadMensagemSocket = new Thread(runnableMensagemSocket);
        this.threadMensagemSocket.start();
    }
    
    private void fileUploadSocketComeceAOuvir() {
        this.runnableFileUploadSocket = () -> {
            while (servidoresContinuemOuvindo) {
                Socket client = null;
                try {
                    client = this.fileUploadSocket.accept();
                } catch (IOException error) {
                    System.out.println(error.getMessage());
                }
                if (client != null) {
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                        ArquivoUpload fileUpload = (ArquivoUpload) objectInputStream.readObject();
                        String diretorio = informacoesServidor.getDiretorioArquivos();
                        String nomeArquivo = diretorio + fileUpload.getCurrentTimeMillis() + "_" + fileUpload.getNome();
                        if (!new File(diretorio).exists()) {
                            new File(diretorio).mkdirs();
                        }
                        new File(nomeArquivo).createNewFile();
                        try (OutputStream outputStream = new FileOutputStream(nomeArquivo)) {
                            outputStream.write(fileUpload.getFileBytes());
                        }
                    } catch (IOException | ClassNotFoundException error) {
                        try {
                            System.out.println(error.getMessage());
                            client.getOutputStream().close();
                            client.getInputStream().close();
                            client.close();
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
        };
        this.threadFileUploadSocket = new Thread(this.runnableFileUploadSocket);
        this.threadFileUploadSocket.start();
    }
    
    private void fileDownloadSocketComeceAOuvir() {
        this.runnableFileDownloadSocket = () -> {
            while (servidoresContinuemOuvindo) {
                Socket client = null;
                try {
                    client = this.fileDownloadSocket.accept();
                } catch (IOException error) {
                    System.out.println(error.getMessage());
                }
                if (client != null) {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String caminhoArquivoNoServidor;
                        if ((caminhoArquivoNoServidor = input.readLine()) != null) {
                            File file = new File(caminhoArquivoNoServidor);
                            byte[] fileBytes = new byte[(int)file.length()];
                            FileInputStream fileInputStream = new FileInputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                            bufferedInputStream.read(fileBytes, 0, fileBytes.length);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                            objectOutputStream.writeObject(new ArquivoDownload(ComandoEnum.ARQUIVODOWNLOAD, null, fileBytes));
                            objectOutputStream.flush();
                        }
                    } catch (IOException error) {
                        try {
                            System.out.println(error.getMessage());
                            client.getOutputStream().close();
                            client.getInputStream().close();
                            client.close();
                        } catch (IOException error2) {
                            System.out.println(error2.getMessage());
                        }
                    }
                }
            }
        };
        this.threadFileDownloadSocket = new Thread(this.runnableFileDownloadSocket);
        this.threadFileDownloadSocket.start();
    }
    
    private InformacoesCliente registrarCliente(Comandos comandoObj) {
        return this.informacoesServidor.addCliente(comandoObj.getInformacoesCliente());
    }
    
    public void servidoresParemDeOuvir() {
        Iterator<ObjectOutputStream> iterador = outEuEOutrosClientes.iterator();
        for (int i = 0; iterador.hasNext(); i++) {
            try {
                ObjectOutputStream outOutroCliente = iterador.next();
                InformacoesCliente informacoesCliente = this.informacoesServidor.getCliente(i);
                outOutroCliente.writeObject(new Comandos(ComandoEnum.DESCONECTAR, informacoesCliente));
                outOutroCliente.flush();
            } catch (IOException error) {
                System.out.println(error.getMessage());
            }
        }
        this.servidoresContinuemOuvindo = false;
    }

    void desconectar(ObjectOutputStream outOutroCliente, InformacoesCliente cliente) { // Desconecta e remove o cliente
        outEuEOutrosClientes.removeIf(c -> c.equals(outOutroCliente));
        this.informacoesServidor.removeCliente(cliente); 
    }
}
