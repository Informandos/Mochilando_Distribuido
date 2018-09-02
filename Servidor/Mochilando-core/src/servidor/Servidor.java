/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import adapter.Adapter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import util.db.exception.ExcecaoNegocio;
import util.db.exception.ExcecaoPersistencia;

/**
 *
 * @author Juliana Carvalho de Souza
 */
/*
    Esta classe é responsável por tratar os pacotes (datagramas) vindos do cliente
    Recebe os pacotes e manda os dados para o adapter que é quem vai decidir o que criar
    de acordo com os parametros do pacote

 */
public class Servidor {

    private static DatagramSocket servidorDatagramaSocket = null;
    private static final int PORTASERVIDOR = 2223;
    private static final int TAMANHO_MAXIMO_DATAGRAMA_UDP = 65507;

    public static void main(String args[]) throws IOException, ClassNotFoundException, ExcecaoPersistencia, ExcecaoNegocio {

        servidorDatagramaSocket = new DatagramSocket(PORTASERVIDOR);
        //Servidor eternamente ligado (enquanto a main estiver rodando)
        while (true) {
            /*Preparando recebimento do pacote (datagrama)*/

            //Vetor de bytes a ser recebido do cliente
            byte[] vetorBytesVindoCliente = new byte[TAMANHO_MAXIMO_DATAGRAMA_UDP];

            //Criando pacote  a se receber do cliente
            DatagramPacket datagramaReceber;
            datagramaReceber = new DatagramPacket(vetorBytesVindoCliente, vetorBytesVindoCliente.length);

            //Servidor aguarda envio de pacotes

            /*Recebendo pacote do cliente, quando o cliente enviar*/
            //Recebendo pacote (copia o pacote para essa variavel)
            servidorDatagramaSocket.receive(datagramaReceber);

            //Passa o pacote para o adapter tratar
            //Adapter deve tratar o pacote e enviar para o cliente
            Adapter adapter = new Adapter(datagramaReceber);
            adapter.tratarRequisicao();
            Thread adapterThread = new Thread(adapter);
            //Adapter deve enviar para o cliente no metodo run
            adapterThread.start();

        }

    }
}
