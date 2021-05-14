/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apsredes.classes;

public abstract class Informacoes {
    private String ipPublicoServidor;
    private String ipLocalServidor;
    private final int portServidor = 57311;
    private final int portFileUpload = 57312;
    private final int portFileDownload = 57313;
    private final String diretorioArquivos = "./downloadsServidor/";
    
    public String getIpPublicoServidor() {
        return ipPublicoServidor;
    }
    public void setIpPublicoServidor(String ipPublicoServidor) {
        this.ipPublicoServidor = ipPublicoServidor;
    }
    public String getIpLocalServidor() {
        return ipLocalServidor;
    }
    public void setIpLocalServidor(String ipLocalServidor) {
        this.ipLocalServidor = ipLocalServidor;
    }
    public int getPortServidor() {
        return portServidor;
    }
    public int getPortFileUpload() {
        return portFileUpload;
    }
    public int getPortFileDownload() {
        return portFileDownload;
    }
    public String getDiretorioArquivos() {
        return diretorioArquivos;
    }
}