/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aluno
 */
public class Cliente {

    public static Cliente cliente;

    private final DatagramSocket clienteDatagramaSocket;
    private final String nomServidorStr = "localhost";
    private final int PORTA = 2233;
    private final InetAddress enderecoIP;
    private final int TAMANHO_MAXIMO_DATAGRAMA_UDP = 65507;

    //ArrayList a ser recebida do proxy
    private ArrayList arrayListVindoProxy = null;
    //ArrayList a ser devolvida para o proxy
    private ArrayList arrayListDestinadoProxy = null;

    private Cliente() throws SocketException, UnknownHostException {
        clienteDatagramaSocket = new DatagramSocket();
        enderecoIP = InetAddress.getByName(nomServidorStr);
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
    public ArrayList requisicao(ArrayList arrayListVindo) throws IOException  {
        //Recebe arrayList do proxy
        this.arrayListVindoProxy = arrayListVindo;
        //Transforma ArrayList em vetor de bytes
        
        byte[] vetorBytesSaida = new byte[TAMANHO_MAXIMO_DATAGRAMA_UDP];
        
        byte[] vetorBytesEntrada = new byte[TAMANHO_MAXIMO_DATAGRAMA_UDP];
        
        
        ByteArrayOutputStream baos;
        ObjectOutputStream writer = null;
        
        baos = new ByteArrayOutputStream();  
        try {
            writer = new ObjectOutputStream(baos);
            writer.writeObject(arrayListVindoProxy);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        writer.close();
        baos.close();
        
        vetorBytesSaida = baos.toByteArray();
        
        
        //Finalmente envia para o servidor
 
        DatagramPacket datagramaEnviar;
        datagramaEnviar = new DatagramPacket(vetorBytesSaida, vetorBytesSaida.length, enderecoIP, PORTA);
        clienteDatagramaSocket.send(datagramaEnviar);
        
        //Recebendo byte[] do servidor e transformando para object(ArrayList)
        
        DatagramPacket datagramaReceber = new DatagramPacket(vetorBytesEntrada, vetorBytesEntrada.length);
        clienteDatagramaSocket.receive(datagramaReceber);
   
 
        try {
            ByteArrayInputStream bais= new ByteArrayInputStream(vetorBytesEntrada);
            ObjectInputStream ois = new ObjectInputStream(bais);
            arrayListDestinadoProxy = (ArrayList) ois.readObject();
            
            bais.close();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro de IO ou ClassNotFoundException ");
        }
         
        
        return arrayListDestinadoProxy;
    }

}
