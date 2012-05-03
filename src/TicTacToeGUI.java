/**
 * Author:  Kevin Richardson <kevin@magically.us>
 * Date:    2011-Dec-9
 * Time:    10:00 PM
 *
 * The GUI for the TicTacToe assignment.  This class will create
 * a form allowing the user to interact with the TicTacToe Server.
 */

package TicTacToe;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class TicTacToeGUI
{
    private final String SERVER_IP    = "127.0.0.1";
    private final int    SERVER_PORT  = 9999;

    private final String FREE_ICON     = "resources/free.png";
    private final String PLAYER_ICON   = "resources/green.jpg";
    private final String COMPUTER_ICON = "resources/yellow.jpg";

    private int winCount = 0, lossCount = 0, tieCount = 0;

    private JFrame frame;
    private Container content;
    private JPanel buttonPanel, optionsPanel;
    private JButton[] buttons;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Handles a click on an enabled grid button.
    private ActionListener gridClickListener = new ActionListener()
    {
        // Transmit the user's desired grid to the server.
        public void actionPerformed(ActionEvent actionEvent)
        {
            String buttonNumber = actionEvent.getActionCommand();
            System.out.println("Sending to server: " + buttonNumber);
            out.println(buttonNumber + "\n");

            // Process any commands sent by the server (which should be a gridStatus string).
            processServerCommands();
        }
    };

    // Handles a click on the new game/close game buttons.
    private ActionListener optionsClickListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            String buttonCommand = actionEvent.getActionCommand();

            // If the user desires to close the game, send the termination
            // string to the server then close the GUI.
            if(buttonCommand.equals("close"))
            {
                try
                {
                    out.println("#CG\n");
                    out.close();
                    in.close();
                    socket.close();
                }
                
                catch(IOException e)
                {
                    System.err.println("Error disconnecting from the TTT server.");
                }

                System.exit(0);
            }
            
            // Otherwise, the user desires a new game.
            // Send the new game string to the server and refresh the grid of buttons.
            else
            {
                showGrid();
                out.println("#NG\n");
                processServerCommands();
            }
        }
    };


    /**
     * Establish any necessary server connections.
     * Run the game for the user.
     * @throws InterruptedException
     */
    public void run() throws InterruptedException
    {
        // Connect to the TicTacToe server.
        connectServer();

        // Load the game's GUI.
        showGUI();

        // Allow the user to play the game.
        processServerCommands();
    }

    /**
     * Create a connection to the TTT server defined in the settings.
     * If successful, create input and output streams to read from and write to
     * the server.
     */
    private void connectServer()
    {
        try
        {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        catch(UnknownHostException e)
        {
            System.err.println("The specified server host could not be found.");
            System.exit(-1);
        }

        catch(IOException e)
        {
            System.err.println("An I/O error has occurred.  Please ascertain the TTT server is running.");
            System.exit(-1);
        }

        System.out.println("The GUI has connected to the server.");
    }

    /**
     * Processes any commands issued by the server.
     *
     * possible commands:
     * a 9 character gridStatus -- see updateGrid()
     *  => expects client to return an integer 0-8 representing the user's move
     * -----
     * "#T" -- game is a tie
     * "#P" -- the player won the game
     * "#C" -- the computer won the game
     *  => expects "#CG" to end the game or "#NG" to create a new game
     *
     */
    private void processServerCommands()
    {
        try
        {
            System.out.println("Processing the server's command...");

            String serverCommand;
            serverCommand = in.readLine();
            System.err.println("Message from server: " + serverCommand);

            // Process any gridStatus strings.
            if(serverCommand.charAt(0) != '#')
            {
                updateGrid(serverCommand);
            }

            // The game has ended.  Show the user his or her statistics.
            else
            {
                showOptions();

                // Alert the user to his or her result.
                String title, text;

                if(serverCommand.equals("#T"))
                {
                    tieCount++;
                    title = "will and fear are balanced.!";
                    text = "This battle ended in a draw.";
                }

                else if(serverCommand.equals("#P"))
                {
                    winCount++;
                    title = "the force of will triumphs!";
                    text = "You have won this battle.";
                }

                else
                {
                    lossCount++;
                    title = "you have given into fear!";
                    text = "You have lost this battle.";
                }

                text += "\nwins: " + winCount + ", ties: " + tieCount + ", losses: " + lossCount;


                JOptionPane.showMessageDialog(null, text, title, JOptionPane.PLAIN_MESSAGE);
            }

            System.out.println("==> Control has returned to the user.");
        }
        
        catch(IOException e)
        {
            System.err.println("Error reading commands from the server.");
        }
    }


    /**
     * Show the user the game's GUI.
     * @throws InterruptedException
     */
    private void showGUI() throws InterruptedException
    {
        showGrid();
        frame.setVisible(true);
    }

    /**
     * Updates the GUI to show the "game over" screen with options to
     * close the game or start a new one.
     */
    private void showOptions()
    {
        content.remove(buttonPanel);
        content.add(optionsPanel);
        optionsPanel.setVisible(true);
        optionsPanel.updateUI();

    }

    /**
     * Updates the GUI to show the grid of buttons (and hide the optionsPanel
     * if it is on screen).
     */
    private void showGrid()
    {
        content.remove(optionsPanel);
        content.add(buttonPanel);
        buttonPanel.setVisible(true);
        buttonPanel.updateUI();
    }

    /**
     * Based on the gridState string, modify the buttons grid to be
     * enabled or disabled and display appropriate pictures for player
     * and computer.
     *
     * @param gridState
     * gridState is a string with length 9 (representing grid[0] to grid[8])
     * with each character representing each box of the grid.
     * player:     "1"
     * computer:   "2"
     * free space: "-"
     */
    private void updateGrid(String gridState)
    {
        for(int i = 0; i < 9; i++)
        {
            JButton button = buttons[i];
            char state = gridState.charAt(i);
            
            // The button is a free space.
            if(state == '-')
            {
                button.setEnabled(true);
            }
                
            // The button is taken.  Determine how to mark the grid (player or computer).
            else
            {
                String icon;
                if(state == '1') icon = PLAYER_ICON;
                else icon = COMPUTER_ICON;

                button.setDisabledIcon(new ImageIcon(getClass().getResource(icon)));
                button.setEnabled(false);
            }
        }       
    }

    /**
     * Holds our main GUI frame object and its components.
     */
    TicTacToeGUI()
    {
        // The frame holds everything else in the GUI.
        frame = new JFrame("Sinestro Corps War");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        /**
         * Define the panels used in this GUI.
         */

        // Panel holding the various buttons representing the tic tac toe grid.
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));

        buttons = new JButton[9];
        for(int i = 0; i < buttons.length; i++)
        {
            JButton button = new JButton(new ImageIcon(getClass().getResource(FREE_ICON)));
            button.setActionCommand(i + "");
            button.addActionListener(gridClickListener);

            buttons[i] = button;
            buttonPanel.add(button);
        }


        // Panel showing buttons allowing user to start/decline new game.
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(2, 1));

        JButton newGameButton = new JButton("new game");
        newGameButton.setActionCommand("new");
        newGameButton.addActionListener(optionsClickListener);

        JButton closeGameButton = new JButton("close game");
        closeGameButton.setActionCommand("close");
        closeGameButton.addActionListener(optionsClickListener);

        optionsPanel.add(newGameButton);
        optionsPanel.add(closeGameButton);
        optionsPanel.setVisible(false);


        // Prepare the content panel of the frame.
        content = frame.getContentPane();
    }

}
