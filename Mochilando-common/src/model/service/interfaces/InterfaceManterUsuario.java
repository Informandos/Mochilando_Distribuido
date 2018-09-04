
package model.service.interfaces;


import java.util.List;
import model.domain.Usuario;
import util.db.exception.*;

/**
 *
 * @author User
 */
public interface InterfaceManterUsuario {
    public Long cadastrar(Usuario usuario) throws  ExcecaoPersistencia,ExcecaoNegocio,ExcecaoConexaoCliente; 
    public boolean alterar(Usuario usuario) throws ExcecaoPersistencia,ExcecaoNegocio,ExcecaoConexaoCliente;
    public boolean excluir(Usuario usuario) throws ExcecaoPersistencia,ExcecaoNegocio,ExcecaoConexaoCliente;
    public List<Usuario> pesquisarTodos() throws ExcecaoPersistencia,ExcecaoConexaoCliente;
    public Usuario pesquisarPorId(Long id) throws ExcecaoPersistencia,ExcecaoConexaoCliente;
    public Usuario getUserLogin(String email, String senha) throws ExcecaoPersistencia,ExcecaoNegocio,ExcecaoConexaoCliente;
     public Usuario getUserEmail(String email) throws ExcecaoPersistencia,ExcecaoNegocio,ExcecaoConexaoCliente;
}