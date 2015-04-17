package jump61;

import java.util.ArrayList;
import java.util.Random;
/** An automated Player.
 *  @author Mingtao Fang
 */
class AI extends Player {

    /** Time allotted to all but final search depth (milliseconds). */
    private static final long TIME_LIMIT = 15000;

    /** Number of calls to minmax between checks of elapsed time. */
    private static final long TIME_CHECK_INTERVAL = 10000;

    /** Number of milliseconds in one second. */
    private static final double MILLIS = 1000.0;

    /** ENUM Strategy. */
    private static enum Strategy { TWO_TWO, RANDOM_VALID, GREEDY };

    /** Strategy. */
    private static Strategy strategy = Strategy.GREEDY;
    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        if (strategy == Strategy.TWO_TWO) {
            getGame().makeMove(2, 2);
            getGame().reportMove(this.getSide(), 2, 2);
        } else if (strategy == Strategy.RANDOM_VALID) {
            Random rn = new Random();
            int randomRow = rn.nextInt(getBoard().size()) + 1;
            int randomCol = rn.nextInt(getBoard().size()) + 1;
            while (!getBoard().isLegal(this.getSide(), randomRow, randomCol)) {
                randomRow = rn.nextInt(getBoard().size()) + 1;
                randomCol = rn.nextInt(getBoard().size()) + 1;
            }
            getGame().makeMove(randomRow, randomCol);
            getGame().reportMove(this.getSide(), randomRow, randomCol);
        } else if (strategy == Strategy.GREEDY) {
            ArrayList<int[]> movesWeDid;
            movesWeDid = getAllPossibleMoves(getSide(), getBoard());
            minmax(getSide(), getBoard(), 4, Integer.MAX_VALUE, movesWeDid);
            getGame().makeMove((movesWeDid.get(0))[0], (movesWeDid.get(0))[1]);
            getGame().reportMove(this.getSide(),
                                (movesWeDid.get(0))[0], (movesWeDid.get(0))[1]);
        }



    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int minmax(Side p, Board b, int d, int cutoff,
                       ArrayList<int[]> moves) {
        int wonMove = Integer.MAX_VALUE;
        int lostMove = Integer.MIN_VALUE;
        ArrayList<int[]> possibleMoves = getAllPossibleMoves(p, b);
        ArrayList<int[]> laterMoves = null;
        MutableBoard bb = new MutableBoard(b);

        if (b.getWinner() != null) {
            if (b.getWinner().equals(p)) {
                return wonMove;
            } else if (b.getWinner().equals(p.opposite())) {
                return lostMove;
            }
        } else if (d == 0) {
            return staticEval(p, b);
        }




        int[] bestMoveSoFar = new int[] {-1, -1, Integer.MIN_VALUE};

        for (int[] move : possibleMoves) {
            int nowRow = move[0];
            int nowCol = move[1];
            int nowScore = move[2];
            bb.addSpot(p, nowRow, nowCol);
            int highestStaticVal = minmax(p.opposite(), b, d - 1,
                                            -nowScore, laterMoves);
            if (-highestStaticVal > nowScore) {
                bestMoveSoFar[0] = nowRow;
                bestMoveSoFar[1] = nowCol;
                bestMoveSoFar[2] = highestStaticVal;
            }
            bb.undo();
        }

        if (moves != null) {
            int[] moveWeWant = bestMoveSoFar;
            moves.add(moveWeWant);
        }

        if (bestMoveSoFar[2] < cutoff) {
            return bestMoveSoFar[2];
        } else {
            return cutoff;
        }

    }





    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side p, Board b) {
        return b.numOfSide(p) - b.numOfSide(p.opposite());
    }



    /** Helper function that take in side WHO and
     *  board B and return allpossiblemoves. */
    private ArrayList<int[]> getAllPossibleMoves(Side who, Board b) {
        ArrayList<int[]> possibleMove = new ArrayList<int[]>();
        int[] position = new int[3];
        for (int r = 1; r <= b.size(); r++) {
            for (int c = 1; c <= b.size(); c++) {
                if (b.isLegal(who, r, c)) {
                    position[0] = r;
                    position[1] = c;
                    position[2] = Integer.MIN_VALUE;
                    possibleMove.add(position);
                }
            }
        }
        return possibleMove;
    }





}



