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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Atracao;
import model.domain.AvaliacaoComentario;
import model.domain.AvaliacaoDiario;
import model.domain.Cidade;
import model.domain.Comentario;
import model.domain.Diario;
import model.domain.Estado;
import model.domain.TipoAtracao;
import model.domain.Usuario;
import model.service.implementacao.ManterAtracao;
import model.service.implementacao.ManterAvaliacaoComentario;
import model.service.implementacao.ManterAvaliacaoDiario;
import model.service.implementacao.ManterUsuario;
import model.service.interfaces.InterfaceManterAtracao;
import model.service.interfaces.InterfaceManterAvaliacaoComentario;
import model.service.interfaces.InterfaceManterAvaliacaoDiario;
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
         
            case "Atracao":
                InterfaceManterAtracao manterAtracao = new ManterAtracao();
                operacao = (String) requisicao.get(1);

                if (operacao.equals("cadastrar")) {
                    Atracao atr = (Atracao) requisicao.get(2);
                    Long codAtr = manterAtracao.cadastrar(atr);
                    //Enviando somente o valor de codUsuario; 
                    //nao precisa de passar o nome do parametro pois o cliente ja sabe o que espera
                    if (resposta == null) {
                        resposta.add(codAtr);
                    }

                } else if (operacao.equals("alterar")) {
                    Atracao atr = (Atracao) requisicao.get(2);
                   boolean sucesso = manterAtracao.alterar(atr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                } else if (operacao.equals("excluir")) {
                    Atracao atr = (Atracao) requisicao.get(2);
                   boolean sucesso = manterAtracao.excluir(atr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                }else if (operacao.equals("pesquisarPorId")) {
                   Atracao atr = (Atracao) requisicao.get(2);
                   Long id = atr.getSeqAtracao();
                   Atracao sucesso = manterAtracao.pesquisarPorId(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                }else if (operacao.equals("pesquisarPorCodCidade")) {
                   Atracao atr = (Atracao) requisicao.get(2);
                   Cidade cidade = atr.getCidade();
                   Long id = cidade.getCodCidade();
                   List<Atracao> sucesso = manterAtracao.pesquisarPorCodCidade(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                }else if (operacao.equals("pesquisarPorCodEstado")) {
                   Atracao atr = (Atracao) requisicao.get(2);
                   Cidade cidade = atr.getCidade();
                   Estado estado = cidade.getEstado();
                   Long id = estado.getCodEstado();
                   List<Atracao> sucesso = manterAtracao.pesquisarPorCodEstado(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                }else if (operacao.equals("pesquisarPorCodTipoAtracao")) {
                   Atracao atr = (Atracao) requisicao.get(2);
                   TipoAtracao tatr = atr.getTipoAtracao();
                   Long codTipoAtracao = tatr.getCodTipoAtracao();
                   List<Atracao> sucesso = manterAtracao.pesquisarPorCodTipoAtracao(codTipoAtracao);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }else if (operacao.equals("pesquisarTodos")) {
                   List<Atracao> sucesso = manterAtracao.pesquisarTodos();
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }
                break;
             case "AvaliacaoComentario":
                InterfaceManterAvaliacaoComentario manterAvaliacaoComentario = new ManterAvaliacaoComentario();
                operacao = (String) requisicao.get(1);    
                if (operacao.equals("cadastrar")) {
                    AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                    Long codAtr = manterAvaliacaoComentario.cadastrar(atr);
                    //Enviando somente o valor de codUsuario; 
                    //nao precisa de passar o nome do parametro pois o cliente ja sabe o que espera
                    if (resposta == null) {
                        resposta.add(codAtr);
                    }

                } else if (operacao.equals("alterar")) {
                    AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   boolean sucesso = manterAvaliacaoComentario.alterar(atr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                } else if (operacao.equals("excluir")) {
                    AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   boolean sucesso = manterAvaliacaoComentario.excluir(atr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                }else if (operacao.equals("pesquisarPorId")) {
                   AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   Long id = atr.getSeqAvaliacao();
                   AvaliacaoComentario sucesso = manterAvaliacaoComentario.pesquisarPorId(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }
                    else if (operacao.equals("pesquisarNumAvPositivas")) {
                   AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   Comentario comentario = atr.getComentario();
                   Long id = comentario.getSeqComentario();
                   int sucesso = manterAvaliacaoComentario.pesquisarNumAvPositivas(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                } else if (operacao.equals("pesquisarNumAvNegativas")) {
                   AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   Comentario comentario = atr.getComentario();
                   Long id = comentario.getSeqComentario();
                   int sucesso = manterAvaliacaoComentario.pesquisarNumAvNegativas(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }else if (operacao.equals("pesquisarPorDiario")) {
                   AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   Comentario comentario = atr.getComentario();
                   Diario diario = comentario.getDiario();
                   Long id = diario.getCodDiario();
                   List<AvaliacaoComentario> sucesso = manterAvaliacaoComentario.pesquisarPorDiario(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }else if (operacao.equals("pesquisarTodos")) {
                   AvaliacaoComentario atr = (AvaliacaoComentario) requisicao.get(2);
                   Long id = atr.getSeqAvaliacao();
                   List<AvaliacaoComentario> sucesso = manterAvaliacaoComentario.pesquisarTodos(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }
                break;
             case "AvaliacaoDiario":
                InterfaceManterAvaliacaoDiario manterAvaliacaoDiario = new ManterAvaliacaoDiario();
                operacao = (String) requisicao.get(1);    
                    
                if (operacao.equals("cadastrar")) {
                    AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                    Long codAtr = manterAvaliacaoDiario.cadastrar(atr);
                    //Enviando somente o valor de codUsuario; 
                    //nao precisa de passar o nome do parametro pois o cliente ja sabe o que espera
                    if (resposta == null) {
                        resposta.add(codAtr);
                    }

                } else if (operacao.equals("alterar")) {
                    AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                   boolean sucesso = manterAvaliacaoDiario.alterar(atr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                } else if (operacao.equals("excluir")) {
                    AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                   boolean sucesso =  manterAvaliacaoDiario.excluir(atr);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    } 
                }else if (operacao.equals("pesquisarPorId")) {
                   AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                   Long id = atr.getSeqAvaliacao();
                   AvaliacaoDiario sucesso =  manterAvaliacaoDiario.pesquisarPorId(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }
                    else if (operacao.equals("pesquisarNumAvPositivas")) {
                   AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                  
                   Long id = atr.getSeqAvaliacao();
                   int sucesso =  manterAvaliacaoDiario.pesquisarNumAvPositivas(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                } else if (operacao.equals("pesquisarNumAvNegativas")) {
                   AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                    Long id = atr.getSeqAvaliacao();
                   int sucesso =  manterAvaliacaoDiario.pesquisarNumAvNegativas(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }else if (operacao.equals("pesquisarPorDiario")) {
                   AvaliacaoDiario atr = (AvaliacaoDiario) requisicao.get(2);
                   
                   Long id = atr.getSeqAvaliacao();
                   List<AvaliacaoDiario> sucesso =  manterAvaliacaoDiario.pesquisarPorDiario(id);
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }else if (operacao.equals("pesquisarTodos")) {
                  
                   List<AvaliacaoDiario> sucesso =  manterAvaliacaoDiario.pesquisarTodos();
                    if (resposta == null) {
                        resposta.add(sucesso);
                    }
                }
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
