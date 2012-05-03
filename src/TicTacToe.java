/**
 * User:    Kevin Richardson <kevin@magically.us>
 * Date:    2011-Dec-9
 * Time:    9:15 PM
 *
 * The main TicTacToe class, storing game state information and relevant methods.
 * The "AI" in this game will determine the best move at every turn.
 */

package TicTacToe;

import java.util.Random;

public class TicTacToe implements Cloneable
{
    /**
     * Various constants used to track grid state or the active player.
     */
    // Constants used to refer to a move made by player or computer.
    private final int NOBODY_TURN   = 0;
    private final int PLAYER_TURN   = 1;
    private final int COMPUTER_TURN = -1;

    // The mark denoting on the printed grid the moves made by player or computer.
    private final char NOBODY_MARK   = ' ';
    private final char PLAYER_MARK   = 'X';
    private final char COMPUTER_MARK = 'O';

    // The difficulty of the game.  This value is used by bestGuess().
    private final int GAME_LEVEL = 8;

    /**
     * An integer (using the ..._TURN constants above) describing whose turn it is to make a move.
     */
    private int whoseTurn = NOBODY_TURN;

    /**
     * An integer (using the ..._TURN constants above) describing who will begin the game.
     */
    private int firstTurn = NOBODY_TURN;

    /**
     * An array representing the current game's board state as a grid.
     */
    private char[] grid = new char[9];

    /**
     * An array representing the current set of moves made in this game.
     * moves[i] will contain a 0 if grid i is unavailable or a 1 if it is available.
     */
    private int[] moves = new int[9];

    /**
     * Represents how many moves have been made during the course of the game.
     */
    private int numMoves = 0;


    /**
     * Establishes a new game state by clearing the board.
     */
    public TicTacToe()
    {
        // Fill out the board so it is empty.
        for(int i = 0; i < 9; i++)
        {
            grid[i] = NOBODY_MARK;
        }

        generateMoves();
    }

    /**
     * Returns a clone of this object.
     */
    public TicTacToe clone() throws CloneNotSupportedException
    {
        // Need to clone the arrays since they're treated as objects in Java.
        TicTacToe clone = (TicTacToe)super.clone();
        clone.grid = this.grid.clone();
        clone.moves = this.moves.clone();

        return clone;
    }

    /**
     * Randomly determines who will go first in this game and return a String describing
     * the player.
     */
    public void chooseFirstPlayer()
    {
        // Randomly determine whether the player or computer will get to go first during this game.
        Random generator = new Random();

        if(generator.nextInt(2) == 0)
        {
            setFirstTurn(PLAYER_TURN);
        }

        else
        {
            setFirstTurn(COMPUTER_TURN);
        }
    }

    /**
     * Returns a String describing the state of the board.
     * This string can be used by the client to draw the board.
     */
    public String drawBoard()
    {
        String toReturn = "";

        for(char space : grid)
        {
            if(space == PLAYER_MARK) toReturn += "1";
            else if(space == COMPUTER_MARK) toReturn += "2";
            else toReturn += "-";
        }

        return toReturn + "\n";
    }


    /**
     * Returns an array containing a list of all possible moves for this game state.
     */
    public int[] generateMoves()
    {
        // Generate the list of possible moves and store them in moves.
        for(int i = 0; i < moves.length; i++)
        {
            // If it is not taken, mark the square as available.  Otherwise, it is unavailable.
            moves[i] = (grid[i] == NOBODY_MARK) ? 1 : 0;
        }

        return moves;
    }

    /**
     * Returns an array containing a list of all remaining, legal moves for this game state.
     */
    public int[] generateLegalMoves()
    {
        // Create the list of legal moves from the move set.
        int[] legalMoves = new int[0];

        for(int i = 0; i < moves.length; i++)
        {
            // If the move is available, add it to the legal moves array.
            if(moves[i] == 1)
            {
                // Increase size of the legal moves array.
                int[] tempLegalMoves = legalMoves;
                legalMoves = new int[tempLegalMoves.length + 1];

                for(int j = 0; j < tempLegalMoves.length; j++)
                {
                    legalMoves[j] = tempLegalMoves[j];
                }

                legalMoves[legalMoves.length - 1] = i;
            }
        }

        return legalMoves;
    }

    /**
     * Returns a boolean value regarding whether or not a proposed move is legal.
     */
    public boolean legalMove(int move)
    {
        // Return false if the move is outside the bounds of the game.
        if(move < 0 || move > 8)
        {
            return false;
        }

        // Returns true if the move is available and false if it is not.
        return moves[move] == 1;
    }

    /**
     * The method through which the computer generates its move.  This method will also make the move
     * for the computer.
     */
    public void computerMove() throws CloneNotSupportedException
    {
        int computerMove = bestMove();

        placePiece(COMPUTER_TURN, computerMove);
    }

    /**
     * Attempts to determine the best move for the computer to make based on the current board state.
     * Returns the best possible move as an integer.
     * @return int
     * @throws CloneNotSupportedException should the TicTacToe objects not clone successfully.
     */
    public int bestMove() throws CloneNotSupportedException
    {
        /**
         * Keeps track of the guess value of the best move and the guess value
         * of the current move being examined.
         */
        int bestGuessValue, currentGuessValue;

        /**
         * Stores the best move found so far and the next move to try.
         */
        int best, tryMove;

        /**
         * Holds a copy of the current game.
         */
        TicTacToe tempSituation;

        /**
         * Stores the set of legal moves for this board state.
         */
        int[] legalMoves;


        // Copy the current boardstate and generate a list of legal moves the computer will attempt to make.
        tempSituation = this.clone();
        legalMoves = tempSituation.generateLegalMoves();


        // Start determining the best move at the first legal move in the set.  Place the piece and determine an
        // estimate of how likely the computer is to win the game by making it.
        tryMove = legalMoves[0];

        tempSituation.placePiece(COMPUTER_TURN, tryMove);
        bestGuessValue = tempSituation.bestGuess(GAME_LEVEL);

        // Track the best move the computer can make.  For now, it has to be the first legal move.
        best = tryMove;


        // Try every remaining possible, legal move to determine which one bestGuess likes the most based on the
        // current board state.
        int currentMoveIndex = 1;
        while(currentMoveIndex < legalMoves.length)
        {
            // Erase the previously placed piece and try the remaining legal moves to determine which gives the best
            // result.
            tempSituation = this.clone();
            tryMove = legalMoves[currentMoveIndex];

            tempSituation.placePiece(COMPUTER_TURN, tryMove);

            // Determine the chance of the computer winning by making this move.
            currentGuessValue = tempSituation.bestGuess(GAME_LEVEL);

            // Choose the move that gives the computer the greatest chance of winning.
            // Do so by taking the move with the highest guess value (closest to winning 100) if computer turn.
            // Otherwise, "block" the move that would allow the player to win (closest to 0).
            if((tempSituation.getWhoseTurn() == COMPUTER_TURN && currentGuessValue > bestGuessValue) ||
                    tempSituation.getWhoseTurn() != COMPUTER_TURN && currentGuessValue < bestGuessValue)
            {
                bestGuessValue = currentGuessValue;
                best = tryMove;
            }

            currentMoveIndex++;
        }

        return best;
    }

    /**
     * Used by bestMove to examine the benefit a move will have based on the current board state.
     * Returns an integer value describing how likely the computer is to win based on the invoking
     * game's board state.
     *
     * 100 - computer has won
     * 50  - tied game
     * 0   - computer has lost
     *
     * level describes how much further the game will play in order to determine the best possible move.
     * Values for level range from 0-8, with 8 being technically unbeatable.
     */
    public int bestGuess(int level) throws CloneNotSupportedException
    {
        /**
         * Keeps track of the guess value of the best move and the guess value
         * of the current move being examined.
         */
        int bestGuessValue, currentGuessValue;

        /**
         * Stores the next move to try.
         */
        int tryMove;

        /**
         * Holds a copy of the current game.
         */
        TicTacToe tempSituation;

        /**
         * Holds a copy of the legal moves for this game.
         */
        int[] legalMoves = this.generateLegalMoves();


        // If we're at the base level or the game is over, return how well the computer has done.
        if((level == 0 || this.isOver()))
        {
            return judge();
        }

        // Copy the current game.
        tempSituation = this.clone();

        // If the level is even, the computer will be trying a move.
        if(level % 2 == 0)
        {
            tempSituation.setWhoseTurn(COMPUTER_TURN);
        }

        // If the level is odd, the player will be trying a move.
        else
        {
            tempSituation.setWhoseTurn(PLAYER_TURN);
        }


        // Place the first legal move and determine how good this move is for the computer.
        tryMove = legalMoves[0];
        tempSituation.placePiece(tempSituation.getWhoseTurn(), tryMove);
        bestGuessValue = tempSituation.bestGuess(level - 1);

        // Run through the remaining legal moves.
        int currentMoveIndex = 1;
        while(currentMoveIndex < legalMoves.length)
        {
            // Erase the previous move and try the next available one.
            tempSituation = this.clone();
            tryMove = legalMoves[currentMoveIndex];
            tempSituation.placePiece(tempSituation.getWhoseTurn(), tryMove);

            currentGuessValue = tempSituation.bestGuess(level - 1);

            // If it is the player's turn, we want to consider taking this move to block him or her from winning.
            if(tempSituation.getWhoseTurn() == PLAYER_TURN)
            {
                bestGuessValue = Math.max(bestGuessValue, currentGuessValue);
            }

            // If it is the computer's turn, we want to take the move that can win.
            else
            {
                bestGuessValue = Math.min(bestGuessValue, currentGuessValue);
            }

            currentMoveIndex++;
        }

        return bestGuessValue;
    }

    /**
     * Updates the game so player ({player}_TURN) makes a move at the specified
     * point in the grid.
     */
    public void placePiece(int player, int move)
    {
        grid[move] = (player == PLAYER_TURN) ? PLAYER_MARK : COMPUTER_MARK;

        numMoves++;

        // Generate the new set of moves taken/not taken in the game.
        generateMoves();
    }

    /**
     * Returns an integer value based on examining the state of the game:
     * 0 - game is on-going
     * 1 - player has won
     * 2 - computer has won
     * 3 - game is a tie
     */
    public int result()
    {
        // First check the columns to see if there are any winners.
        for(int i = 0; i < 3; i++)
        {
            // Has the player won?
            if(grid[i] == PLAYER_MARK && grid[i + 3] == PLAYER_MARK && grid[i + 6] == PLAYER_MARK)
            {
                return 1;
            }

            // Has the computer won?
            else if(grid[i] == COMPUTER_MARK && grid[i + 3] == COMPUTER_MARK && grid[i + 6] == COMPUTER_MARK)
            {
                return 2;
            }
        }

        // Then check rows for any winners.
        for(int i = 0; i <= 6; i += 3)
        {
            // Has the player won?
            if(grid[i] == PLAYER_MARK && grid[i + 1] == PLAYER_MARK && grid[i + 2] == PLAYER_MARK)
            {
                return 1;
            }

            // Has the computer won?
            if(grid[i] == COMPUTER_MARK && grid[i + 1] == COMPUTER_MARK && grid[i + 2] == COMPUTER_MARK)
            {
                return 2;
            }
        }

        // Finally, check the diagonals.
        if(grid[0] == PLAYER_MARK && grid[4] == PLAYER_MARK && grid[8] == PLAYER_MARK)
        {
            return 1;
        }

        else if(grid[2] == PLAYER_MARK && grid[4] == PLAYER_MARK && grid[6] == PLAYER_MARK)
        {
            return 1;
        }


        if(grid[0] == COMPUTER_MARK && grid[4] == COMPUTER_MARK && grid[8] == COMPUTER_MARK)
        {
            return 2;
        }

        else if(grid[2] == COMPUTER_MARK && grid[4] == COMPUTER_MARK && grid[6] == COMPUTER_MARK)
        {
            return 2;
        }

        // If there are 9 moves at this point, the game is a draw.
        if(numMoves == 9)
        {
            return 3;
        }

        // Otherwise, the game continues!
        else
        {
            return 0;
        }
    }

     /**
     * Returns an integer value describing how good the current situation is for the computer.
     * 100: computer has won
     * 50:  computer/player are tied
     * 0:   the player has won
     */
    public int judge()
    {
        switch(result())
        {
            case 0:
                return 50;
            case 1:
                return 0;
            case 2:
                return 100;
            case 3:
                return 50;
        }

        // Should never get here...
        return 50;
    }

    /**
     * Returns true if the game is over or false if it is not.
     */
    public boolean isOver()
    {
      return(result() != 0);
    }


    /**
     * Return the int value of who controls the current turn.
     */
    public int getWhoseTurn()
    {
        return whoseTurn;
    }

    /**
     * Sets the int value of who controls the current turn.
     */
    public void setWhoseTurn(int whoseTurn)
    {
        this.whoseTurn = whoseTurn;
    }

    /**
     * Return the int value of who gets to go first in this game.
     */
    public int getFirstTurn()
    {
        return firstTurn;
    }

    /**
     * Sets the value of who gets to go first in this game.
     */
    public void setFirstTurn(int firstTurn)
    {
        this.firstTurn = firstTurn;
    }

    /**
     * Returns an array of legal moves.
     */
    public int[] getMoves()
    {
        return moves;
    }

    /**
     * Returns the int value representing the player has the first move of the game.
     */
    public int getPLAYER_TURN()
    {
        return PLAYER_TURN;
    }

    /**
     * Returns the int value representing the computer has the first move of the game.
     */
    public int getCOMPUTER_TURN()
    {
        return COMPUTER_TURN;
    }
}
