/**
 * Author:  Kevin Richardson <kevin@magically.us>
 * Date:    2011-Dec-9
 * Time:    3:00 PM
 *
 * The GUI for the TicTacToe assignment.  This class will create
 * a form allowing the user to interact with the TicTacToe Server.
 */

package TicTacToe;

public class PlayTicTacToe
{
    public static void main(String[] args) throws InterruptedException
    {
        TicTacToeGUI game = new TicTacToeGUI();
        game.run();
    }
}
