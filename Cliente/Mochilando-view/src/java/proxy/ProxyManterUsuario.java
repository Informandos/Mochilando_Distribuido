/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import cliente.Cliente;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Usuario;
import model.service.interfaces.InterfaceManterUsuario;
import util.db.exception.ExcecaoNegocio;
import util.db.exception.ExcecaoPersistencia;

/**
 *
 * @author Aluno
 */
public class ProxyManterUsuario implements InterfaceManterUsuario {
    
    private ArrayList manterUsuario = null;
    private Cliente cliente = null;
    /*
            ArrayList armazena:
            1) Tipo do Objeto (String)
            2) Operacao (cadastrar, alterar, excluir, pesquisar, etc)
            3) Parametro(s) da operacao (Objeto, String ou Long)
     */
    public ProxyManterUsuario() throws SocketException, UnknownHostException{
         cliente = Cliente.getInstance();
    }
    
    @Override
    public Long cadastrar(Usuario usuario) throws ExcecaoPersistencia, ExcecaoNegocio {
        manterUsuario = new ArrayList();
        manterUsuario.add("Usuario");
        manterUsuario.add("Cadastrar");
        manterUsuario.add(usuario);
        
        //Indice de onde vai estar o long
        Long codUsuario = 0L;
        try {
            codUsuario = (Long) cliente.requisicao(manterUsuario).get(0);
        } catch (IOException ex) {
            Logger.getLogger(ProxyManterUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codUsuario;
    }

    @Override
    public boolean alterar(Usuario usuario) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean excluir(Usuario usuario) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Usuario> pesquisarTodos() throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Usuario pesquisarPorId(Long id) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Usuario getUserLogin(String email, String senha) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Usuario getUserEmail(String email) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
