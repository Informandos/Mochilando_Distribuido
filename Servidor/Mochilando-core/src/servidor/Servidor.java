/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import adapter.AdapterSwitchCase;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Aluno
 */
public class Servidor {
    public static void main (String args[]) throws IOException {
        
        ServerSocket server = null;
        
        try {
            server = new ServerSocket(2223);

            while(true) {
                Socket socket = server.accept();
                AdapterSwitchCase manterUsuarioAdapter = new AdapterSwitchCase(socket);
                Thread t = new Thread(manterUsuarioAdapter);
                t.start();
            }
        }
        catch(Exception e) {
            if (server != null)
                server.close();
        }
    }
}
