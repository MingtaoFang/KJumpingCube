package jump61;

import static jump61.GameException.error;

/** A Player that gets its moves from manual input.
 *  @author Mingtao Fang
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Side color) {
        super(game, color);
    }

    @Override
    /** Retrieve moves using getGame().getMove() until a legal one is found and
     *  make that move in getGame().  Report erroneous moves to player. */
    void makeMove() {
        int[] move1 = new int[2];
        Board board = getBoard();

        if (getGame().getMove(move1)) {
            int r = move1[0];
            int c = move1[1];
            if (!board.isLegal(board.whoseMove(), r, c)) {
                throw error("invalid move!!!");
            }






            getGame().makeMove(move1[0], move1[1]);
        }
    }

}
