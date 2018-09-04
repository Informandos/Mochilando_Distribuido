package model.service.implementacao;

import util.db.exception.ExcecaoNegocio;
import util.db.exception.ExcecaoPersistencia;
import java.util.List;
import model.dao.implementacao.AvaliacaoComentarioDAO;
import model.dao.interfaces.InterfaceAvaliacaoComentarioDAO;
import model.domain.AvaliacaoComentario;
import model.domain.AvaliacaoDiario;
import model.service.interfaces.InterfaceManterAvaliacaoComentario;

public class ManterAvaliacaoComentario implements InterfaceManterAvaliacaoComentario{

    private final InterfaceAvaliacaoComentarioDAO aComntDAO;

    public ManterAvaliacaoComentario() {
        aComntDAO = new AvaliacaoComentarioDAO();
    }
    
    @Override
    public Long cadastrar(AvaliacaoComentario avaliacaoComentario) throws ExcecaoPersistencia, ExcecaoNegocio {
        if (avaliacaoComentario.getSeqAvaliacao() == null) {
            throw new ExcecaoNegocio("Obrigatório informar o código da avaliação");
        }
        if (avaliacaoComentario.getComentario().getSeqComentario() == null) {
            throw new ExcecaoNegocio("Obrigatório informar o código do comentário");
        }
        if (avaliacaoComentario.getAvaliacao() == null) {
            throw new ExcecaoNegocio("Obrigatório informar a avaliação");
        }
        if (avaliacaoComentario.getUsuario().getCodUsuario() == null) {
            throw new ExcecaoNegocio("Obrigatório informar o código do usuário");
        }
        
        Long result = aComntDAO.inserir(avaliacaoComentario);
        return result;
    }

    @Override
    public boolean alterar(AvaliacaoComentario avaliacaoComentario) throws ExcecaoPersistencia, ExcecaoNegocio {
        if (avaliacaoComentario.getSeqAvaliacao() == null) {
            throw new ExcecaoNegocio("Obrigatório informar o código da avaliação");
        }
        if (avaliacaoComentario.getComentario().getSeqComentario() == null) {
            throw new ExcecaoNegocio("Obrigatório informar o código do comentário");
        }
        if (avaliacaoComentario.getAvaliacao() == null) {
            throw new ExcecaoNegocio("Obrigatório informar a avaliação");
        }
        if (avaliacaoComentario.getUsuario().getCodUsuario() == null) {
            throw new ExcecaoNegocio("Obrigatório informar o código do usuário");
        }
        
        boolean result = aComntDAO.atualizar(avaliacaoComentario);
        return result;
    }

    @Override
    public boolean excluir(AvaliacaoComentario avaliacaoComentario) throws ExcecaoPersistencia, ExcecaoNegocio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AvaliacaoComentario pesquisarPorId(Long seqAvaliacao) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int pesquisarNumAvPositivas(Long seqComentario) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int pesquisarNumAvNegativas(Long seqComentario) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AvaliacaoComentario> pesquisarPorDiario(Long codDiario) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AvaliacaoComentario> pesquisarTodos(Long seqAvaliacao) throws ExcecaoPersistencia {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
