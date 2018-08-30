package proxy;

import cliente.Cliente;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.domain.Dia;
import model.service.interfaces.InterfaceManterDia;
import util.db.exception.ExcecaoNegocio;
import util.db.exception.ExcecaoPersistencia;

public class ProxyManterDia implements InterfaceManterDia{

    private ArrayList manterDia = null;
    private Cliente cliente = null;
    
    public ProxyManterDia() throws SocketException, UnknownHostException{
         cliente = Cliente.getInstance();
    }
    
    @Override
    public Long cadastrar(Dia dia) throws ExcecaoPersistencia, ExcecaoNegocio {
        manterDia = new ArrayList();
        manterDia.add("Dia");
        manterDia.add("cadastrar");
        manterDia.add(dia);
        
        //Indice de onde vai estar o long
        Long result = 0L;
        try {
            result = (Long) cliente.requisicao(manterDia).get(0);
        } catch (IOException ex) {
            Logger.getLogger(ProxyManterDia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public boolean alterar(Dia dia) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean excluir(Dia dia) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dia pesquisarPorId(Long seqDia) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Dia> pesquisarPorCodDiario(Long codDiario) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Dia> pesquisarTodos() throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
