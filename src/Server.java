/**
 * Author:  Kevin Richardson <kevin@magically.us>
 * Date:    2011-Dec-9
 * Time:    9:10 PM
 *
 * This class creates a single-threaded server on specified port that can be accessed by
 * one user's GUI client to play a game of TicTacToe through a ServerGame instance.
 */

package TicTacToe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    /**
     * The port on which this server will run.
     */
    static int PORT = 9999;


    public static void main(String[] args)
    {
        // Have the server run until it is killed.
        while(true)
        {
            // Monitor connections to PORT (if it is available).
            try
            {
                ServerSocket welcomeSocket = new ServerSocket(PORT);
                System.out.println("The server is now running on port " + PORT + "...");


                // When welcomeSocket is contacted, it returns a socket to handle communication
                // with the client.
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("A user has connected from " + connectionSocket.getInetAddress());

                // Establish the client's input stream.
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                // Establish the server's output stream.
                DataOutputStream serverOutput = new DataOutputStream(connectionSocket.getOutputStream());


                // Create a server-game based version of TicTacToe.
                ServerGame game = new ServerGame(clientInput, serverOutput);

                try
                {
                    game.start();
                }

                catch(CloneNotSupportedException e)
                {
                    System.err.println("The gamed failed to start.");
                    System.exit(-1);
                }

                finally
                {
                    connectionSocket.close();
                }
            }

            // Catch any binding/IO errors that may occur.
            catch(IOException e)
            {
                System.err.println(e);
            }
        }
    }

}
