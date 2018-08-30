/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Usuario;
import model.service.implementacao.ManterUsuario;
import model.service.interfaces.InterfaceManterUsuario;
import util.db.exception.ExcecaoNegocio;
import util.db.exception.ExcecaoPersistencia;

/**
 *
 * @author Juliana Carvalho de Souza
 */

/*
    Esta classse e responsavel por tratar o pacote vindo e retornar para o cliente
 */
public class Adapter implements Runnable {

    //Este socket sera utilizado para mandar pacotes ao cliente
    private DatagramSocket adapterDatagramaSocket;
    
    private static final int TAMANHO_MAXIMO_DATAGRAMA_UDP = 65507;
    private final ArrayList requisicao;
    private ArrayList resposta;

    //Vetor de bytes a ser enviado para o cliente
    private byte[] vetorBytesDestinoCliente = new byte[TAMANHO_MAXIMO_DATAGRAMA_UDP];

    //Vetor de bytes a ser recebido do cliente
    private byte[] vetorBytesVindoCliente = new byte[TAMANHO_MAXIMO_DATAGRAMA_UDP];

    //Tamanho do pacote a ser recebido:
    private final int tamanhoPacoteEmBytes;

    //Endereco IP do cliente (usado para mandar resposta)
    InetAddress enderecoIPCliente;

    //Porta do cliente (usado para mandar resposta)
    int portaCliente;

    public Adapter(DatagramPacket datagramaReceber) throws IOException, ClassNotFoundException {
        /* 
            Construtor responsavel por inicializar variaveis de pacote
        */
        
        //Pegando dados do pacote
        vetorBytesVindoCliente = datagramaReceber.getData();

        //Pegando tamanho do pacote
        tamanhoPacoteEmBytes = datagramaReceber.getLength();

        //Pegando endereco IP do cliente
        enderecoIPCliente = datagramaReceber.getAddress();

        //Pegando porta do cliente
        portaCliente = datagramaReceber.getPort();

        System.out.println("Mensagem recebida do cliente de endereco IP: " + enderecoIPCliente + " por meio da porta: " + portaCliente);

        ObjectInputStream ois;
        try (
                //Convertendo vetor de bytes para arraylist
                ByteArrayInputStream bais = new ByteArrayInputStream(vetorBytesVindoCliente)) {
            ois = new ObjectInputStream(bais);
            this.requisicao = (ArrayList) ois.readObject();
        }
        ois.close();

        //O construtor deixa o arraylist requisicao pronto para ser tratado
    }

    public void tratarRequisicao() throws ExcecaoPersistencia, ExcecaoNegocio, IOException {
        String tipoObjeto = (String) requisicao.get(0);
        String operacao;

        switch (tipoObjeto) {
            case "Usuario":
                InterfaceManterUsuario manterUsuario = new ManterUsuario();
                operacao = (String) requisicao.get(1);

                if (operacao.equals("cadastrar")) {
                    Usuario usr = (Usuario) requisicao.get(2);
                    Long codUsr = manterUsuario.cadastrar(usr);
                    //Enviando somente o valor de codUsuario; 
                    //nao precisa de passar o nome do parametro pois o cliente ja sabe o que espera
                    if (resposta == null) {
                        resposta.add(codUsr);
                    }

                } else if (operacao.equals("alterar")) {
                    Usuario usr = (Usuario) requisicao.get(2);
                    boolean sucesso = manterUsuario.alterar(usr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }

                }//Demais if eleses aqui
                break;
            //Outros cases aqui
        }
        //Apos escrever no arrayList, envia a resposta
        enviarResposta();
    }

    public void enviarResposta() throws IOException {
        //Converte arraylis para bytes

        ByteArrayOutputStream baos;
        ObjectOutputStream writer = null;

        baos = new ByteArrayOutputStream();

        try {
            writer = new ObjectOutputStream(baos);
            writer.writeObject(resposta);
            writer.flush();
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(Adapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        vetorBytesDestinoCliente = baos.toByteArray();

        //Finalmente envia para o servidor
        DatagramPacket datagramaEnviar;
        datagramaEnviar = new DatagramPacket(vetorBytesDestinoCliente, vetorBytesDestinoCliente.length, enderecoIPCliente, portaCliente);
        adapterDatagramaSocket.send(datagramaEnviar);

    }

    @Override
    public void run() {
        try {
            tratarRequisicao();

        } catch (ExcecaoPersistencia | ExcecaoNegocio | IOException ex) {
            Logger.getLogger(Adapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
