/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author Aluno
 */
public class Cliente {

    public static Cliente cliente;

    private DatagramSocket clienteDatagramaSocket;
    private final String nomServidorStr = "localhost";
    private final int porta = 2233;
    private InetAddress EnderecoIP;
    private final int tamanhoMaximoDatagramaUDP = 65507;

    //ArrayList a ser recebida do proxy
    private ArrayList arrayListVindoProxy = null;

    private Cliente() throws SocketException, UnknownHostException {
        clienteDatagramaSocket = new DatagramSocket();
        EnderecoIP = InetAddress.getByName(nomServidorStr);
    }

    public static Cliente getInstance() throws SocketException, UnknownHostException {
        if (cliente == null) {
            cliente = new Cliente();
        }
        return cliente;
    }

    /*
    Transforma arrayList em byte (cliente enviando)
    Envia byte para servidor Datagram
    Recebe byte do servidor
    Transforma byte em arrayList (cliente recebendo)
     */
    public ArrayList requisicao(ArrayList arrayListVindo) throws IOException {
        //Recebe arrayList do proxy
        this.arrayListVindoProxy = arrayListVindo;
        //Transforma ArrayList em vetor de bytes
        
        byte[] byteArray = null;
        
        ByteArrayOutputStream baos = null;
        ObjectOutputStream writer = null;
        
        baos = new ByteArrayOutputStream();
        writer = new ObjectOutputStream(baos);
        writer.writeObject(arrayListVindoProxy);
        writer.flush();
        
        byteArray = baos.toByteArray();
        
        //Verifica se o tamanho nao ultrapassa o limite do datagrama
        if(byteArray.length > this.tamanhoMaximoDatagramaUDP){
            System.out.println("Tamanho do pacote excede limite");
            System.exit(0);
        }
        //Finalmente envia para o servidor
        DatagramPacket datagramaEnviar = new DatagramPacket(byteArray, byteArray.length, EnderecoIP, porta);
        clienteDatagramaSocket.send(datagramaEnviar);
        return arrayListVindoProxy;
    }

}
