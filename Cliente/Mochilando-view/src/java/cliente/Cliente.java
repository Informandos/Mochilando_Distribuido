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
import util.conversao.Conversao;

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
    private final int TAMANHO_MAXIMO_DATAGRAMA_UDP = 1500;
    private final int TAMANHO_MAXIMO_CONTEUDO_BYTE = 1000;
    private Conversao conversor;
    private int numeroPacotesEnviar;
    private int numPacotesReceber;

    private byte[] vetorBytesSaida;
    private byte[] vetorBytesEntrada;

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
    public ArrayList requisicao(ArrayList arrayListVindo) throws IOException, ClassNotFoundException {
        //Recebe arrayList do proxy
        this.arrayListVindoProxy = arrayListVindo;

        /*//Transforma ArrayList em vetor de bytes

        vetorBytesSaida = this.arrayListParaByte(arrayListVindoProxy);*/
        //Divide arraylist em vetores de byte menores e envia para o servidor (cada pacote contem um pequeno vetor de bytes)
        //1000 bytes para conteudo, 300 para identificacao e 200 extras para o objeto Pacote
        //Finalmente envia para o servidor
        if (enviaSolicitacaoComunicacao()) {
            ArrayList<Pacote> arrayPacotes = montaPacotes(arrayListVindoProxy);
            for (Pacote pacote : arrayPacotes) {
                enviaPacote(pacote);
            }
            //Depois de enviar os pacotes, pega a resposta
            //O servidor deve primeiro enviar uma permissao para o cliente para 
            //mandar a resposta, informando o numero de pacotes
            recebeSolicitacaoComunicacao();
            ArrayList<Pacote> pacotes = obtemRespostaPacotes();
            //Pega todos os pacotes enviados e monta array list
            this.arrayListDestinadoProxy = desmontaPacotes(pacotes);
        }
        return arrayListDestinadoProxy;
    }

    //metodo que monta pacote
    public ArrayList<Pacote> montaPacotes(ArrayList array) throws IOException {

        ArrayList<Pacote> arrayPacotes = null;

        //calcula o tamanho em bytes do dado
        vetorBytesEntrada = conversor.objetoParaByte(array);
        //Tamanho em bytes do arraylist
        int tamanhoVetBytesArrayList = vetorBytesEntrada.length;
        //Divide o vetor de bytes em vetores menores com tamanho ate 1000
        //Calcula numero de vetoreszinhos necesarios ( se a divisao der exata ok, mas senao, precisara de +1)
        //Pacotes de 1000 bytes
        this.numeroPacotesEnviar = (int) Math.ceil(tamanhoVetBytesArrayList / TAMANHO_MAXIMO_CONTEUDO_BYTE);

        byte[] vet = new byte[TAMANHO_MAXIMO_CONTEUDO_BYTE];

        for (int ind = 0; ind < tamanhoVetBytesArrayList; ind++) {
            // indice do vetor onde sera gravado aquele byte
            int resto = ind % TAMANHO_MAXIMO_CONTEUDO_BYTE;
            vet[resto] = vetorBytesEntrada[ind];
            //Envia o pacote quando o indice for o limite
            if (resto + 1 == TAMANHO_MAXIMO_CONTEUDO_BYTE) {
                //Ordem e 1,2,3
                int ordem = (ind + 1) / TAMANHO_MAXIMO_CONTEUDO_BYTE;
                Pacote pacote = new Pacote(ordem, vet);
                arrayPacotes.add(pacote);
            } else if (ind == tamanhoVetBytesArrayList - 1) {
                int ordem = ind / TAMANHO_MAXIMO_CONTEUDO_BYTE + 1;
                Pacote pacote = new Pacote(ordem, vet);
                arrayPacotes.add(pacote);
            }
        }
        return arrayPacotes;
    }

    public void enviaPacote(Pacote pacote) throws IOException {
        //Converte pacote em byte
        byte[] bytesPacote;
        bytesPacote = conversor.objetoParaByte(pacote);

        //envia pacote
        DatagramPacket datagramaEnviar;
        datagramaEnviar = new DatagramPacket(bytesPacote, bytesPacote.length, enderecoIP, PORTA);
        clienteDatagramaSocket.send(datagramaEnviar);
    }

    public boolean enviaSolicitacaoComunicacao() throws IOException, ClassNotFoundException {
        boolean resposta = false;

        //Passa o numero de pacotes para o servidor, para ele saber quantos serao recebidos
        byte[] cabecalhoBytes = conversor.objetoParaByte(this.numeroPacotesEnviar);
        DatagramPacket primeiroPacote;
        primeiroPacote = new DatagramPacket(cabecalhoBytes, cabecalhoBytes.length, enderecoIP, PORTA);
        clienteDatagramaSocket.send(primeiroPacote);

        //Recebendo resposta (boolean) do servidor 
        DatagramPacket datagramaReceber = new DatagramPacket(vetorBytesEntrada, vetorBytesEntrada.length);
        clienteDatagramaSocket.receive(datagramaReceber);

        //Transformando byte para object(String)
        resposta = (boolean) conversor.byteParaObjeto(vetorBytesEntrada);

        return resposta;

    }

    public void recebeSolicitacaoComunicacao() throws IOException, ClassNotFoundException {

        byte[] conteudoServidorSolicitacao = null;
        //Recebendo numero de pacotes do servidor que quer falar quantos pacotes foram enviados
        DatagramPacket datagramaReceber = new DatagramPacket(conteudoServidorSolicitacao, conteudoServidorSolicitacao.length);
        clienteDatagramaSocket.receive(datagramaReceber);

        //Transformando byte para object(String)
        numPacotesReceber = (int) conversor.byteParaObjeto(conteudoServidorSolicitacao);

        boolean resposta = true;
        byte[] respostaBytes = conversor.objetoParaByte(resposta);
        //enviando resposta de que pode mandar para o servidor
        DatagramPacket respostaSolicitacao;
        respostaSolicitacao = new DatagramPacket(respostaBytes, respostaBytes.length, enderecoIP, PORTA);
        clienteDatagramaSocket.send(respostaSolicitacao);

    }

    public ArrayList<Pacote> obtemRespostaPacotes() throws IOException, ClassNotFoundException {

        //Obtem todos os pacotes em um arraylist
        byte[] bytesPacoteResposta = new byte[TAMANHO_MAXIMO_DATAGRAMA_UDP];
        ArrayList<Pacote> arrayPacotesRecebido = null;

        for (int aux = 0; aux < this.numPacotesReceber; aux++) {

            DatagramPacket datagramaReceberResposta = new DatagramPacket(bytesPacoteResposta, bytesPacoteResposta.length);
            clienteDatagramaSocket.receive(datagramaReceberResposta);
            Pacote pacoteRecebido = (Pacote) conversor.byteParaObjeto(bytesPacoteResposta);
            arrayPacotesRecebido.add(pacoteRecebido);
        }
        return arrayPacotesRecebido;
    }

    public ArrayList desmontaPacotes(ArrayList<Pacote> arrayPacotes) throws IOException, ClassNotFoundException {
        ArrayList respostaRecebida;
        int numBytesArrayList = 0;
        
        
        byte[] bytesRespostaRecebida = null;

        byte[] vetBytesConteudoPacote;
        for (int i = 0; i < this.numPacotesReceber; i++) {
            int maxIterador;
            if (i == this.numPacotesReceber - 1) {
                maxIterador = arrayPacotes.get(i).getConteudo().length;
                
            } else {
                maxIterador = TAMANHO_MAXIMO_CONTEUDO_BYTE;
            }
            for (int j = 0; j < maxIterador; j++) {
                vetBytesConteudoPacote = arrayPacotes.get(i).getConteudo();
                bytesRespostaRecebida[i * this.TAMANHO_MAXIMO_CONTEUDO_BYTE + j] = vetBytesConteudoPacote[j];
            }

        }
        respostaRecebida = (ArrayList) conversor.byteParaObjeto(bytesRespostaRecebida);
        return respostaRecebida;
    }

}
