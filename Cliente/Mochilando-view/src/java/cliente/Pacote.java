/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.util.ArrayList;

/**
 *
 * @author Juliana
 */
public class Pacote {
    
    private final int ordem;
    private final byte[] conteudo;

    public Pacote( int ordem, byte[] conteudo) {
      
        this.ordem = ordem;
        this.conteudo = conteudo;
    }
    
    public byte[] getConteudo(){
        return this.conteudo;
    }
    
}
