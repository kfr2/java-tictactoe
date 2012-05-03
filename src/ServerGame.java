/**
 * Author:  Kevin Richardson <kevin@magically.us>
 * Date:    2011-Dec-9
 * Time:    9:20 PM
 *
 * This class instantiates a TacTacToe game and runs until it is complete.
 * It is used by Server to allow a user to play over the network.
 */

package TicTacToe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerGame
{
    private TicTacToe game;
    private Scanner scanner;
    private BufferedReader input;
    private DataOutputStream output;


    /**
     * Establishes a server-based game of TicTacToe based off specified
     * input and output streams.
     */
    ServerGame(BufferedReader input, DataOutputStream output)
    {
        this.input = input;
        this.output = output;
    }

    // Establish and begin a new game of TicTacToe.
    public void start() throws InputMismatchException, CloneNotSupportedException, IOException
    {
        scanner = new Scanner(input);

        // Establish a new game of TicTacToe!
        game = new TicTacToe();
        game.chooseFirstPlayer();

        // Play until the game is over!
        while(!game.isOver())
        {
            // If the computer has the first move...
            if(game.getFirstTurn() == game.getCOMPUTER_TURN())
            {
                doComputerTurn();

                if(!game.isOver())
                {
                    doPlayerTurn();
                }
            }

            // Otherwise, the player has the first move...
            else
            {
                doPlayerTurn();

                if(!game.isOver())
                {
                    doComputerTurn();
                }
            }
        }

        // The game is over:
        // Print the final board state.
        //output.writeBytes(game.drawBoard());

        // Determine how the game ended and alert user.
        switch(game.result())
        {
            // Player has won.
            case 1:
                System.out.println("The player has won the game.");
                output.writeBytes("#P\n");
                break;
            // Computer has won.
            case 2:
                System.out.println("The computer has won the game.");
                output.writeBytes("#C\n");
                break;
            // Game is a tie.
            case 3:
                System.out.println("The game is a tie.");
                output.writeBytes("#T\n");
                break;
        }


        // Ascertain if user would like to play again.
        System.out.println("Determining if user would like to play another game...");

        String decision = "";

        while(!decision.equals("#NG") && !decision.equals("#CG"))
        {
            decision = input.readLine();
        }

        // If applicable, restart the game for the user.
        if(decision.equals("#NG"))
        {
            System.out.println("The user would like to play another game.");
            this.start();
        }

        // Otherwise, lose the streams and exit back to Server.
        else
        {
            System.out.println("The user would NOT like to play another game.");
            input.close();
            output.close();
        }
    }


    // / Run through the player's turn.
    public void doPlayerTurn() throws IOException
    {
        game.setWhoseTurn(game.getPLAYER_TURN());

        int userMove = -1;

        // Ask the user for his or her move until he or she enters a valid one.
        while(!game.legalMove(userMove))
        {
            output.writeBytes(game.drawBoard());

            // Get the user's desired move.
            try
            {
                userMove = scanner.nextInt();
            }

            // If the user enters a nonInt value, set his or her move equal to -1 (to force reentry).
            // Reset the scanner to avoid endless looping.
            catch(InputMismatchException e)
            {
                userMove = -1;
                scanner = new Scanner(input);
            }
            
            // Gracefully exist should the user close the window or the game otherwise ends suddenly.
            catch(NoSuchElementException e)
            {
                System.err.println("The game was terminated by the user.");
            }
        }

        // Make the user's legal move.
        game.placePiece(game.getPLAYER_TURN(), userMove);
    }


    // Run through the computer's turn.
    public void doComputerTurn() throws CloneNotSupportedException
    {
        game.setWhoseTurn(game.getCOMPUTER_TURN());
        game.computerMove();
    }

}
