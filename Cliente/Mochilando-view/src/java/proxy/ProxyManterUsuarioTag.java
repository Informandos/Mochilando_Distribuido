/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import cliente.Cliente;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import model.domain.UsuarioTag;
import model.service.interfaces.InterfaceManterUsuarioTag;
import util.db.exception.ExcecaoNegocio;
import util.db.exception.ExcecaoPersistencia;

/**
 *
 * @author Aluno
 */
public class ProxyManterUsuarioTag implements InterfaceManterUsuarioTag{
    private ArrayList manterUsuarioTag = null;
    private Cliente cliente = null;
    /*
            ArrayList armazena:
            1) Tipo do Objeto (String)
            2) Operacao (cadastrar, alterar, excluir, pesquisar, etc)
            3) Parametro(s) da operacao (Objeto, String ou Long)
     */
    public ProxyManterUsuarioTag() throws SocketException, UnknownHostException{
         cliente = Cliente.getInstance();
    }

    @Override
    public Long cadastrar(UsuarioTag usuarioTag) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(UsuarioTag usuarioTag) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean excluir(UsuarioTag usuarioTag) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UsuarioTag pesquisarPorId(Long seqUsuarioTag) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UsuarioTag> pesquisarTodos() throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UsuarioTag> pesquisarPorCodUsuario(Long codUsuario) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UsuarioTag> pesquisarPorCodTag(Long codTag) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
