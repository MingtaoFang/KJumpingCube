package jump61;

import static jump61.Side.*;
import static jump61.Square.square;
import java.util.Stack;

/** A Jump61 board state that may be modified.
 *  @author Mingtao
 */
class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _size = N;
        _mutableBoard = new Square[N][N];
        _moves = 0;
        _history = new Stack<Square[][]>();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                _mutableBoard[i][j] = square(Side.WHITE, 1);
            }
        }
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear. */
    MutableBoard(Board board0) {
        copy(board0);

    }
    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    @Override
    void clear(int N) {
        _moves = 0;
        _history = new Stack<Square[][]>();

        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.size(); j++) {
                _mutableBoard[i][j] = square(Side.WHITE, 1);
            }
        }

        announce();
    }

    @Override
    void copy(Board board) {
        _size = board.size();
        _mutableBoard = new Square[board.size()][board.size()];
        _moves = 0;
        _history = new Stack<Square[][]>();

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                this._mutableBoard[i][j] = board.get(i + 1, j + 1);
            }
        }
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history.  Assumes BOARD and I have the same size. */
    private void internalCopy(MutableBoard board) {
        _size = board.size();
        _mutableBoard = new Square[board.size()][board.size()];
        _moves = 0;
        _history = this._history;

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                this._mutableBoard[i][j] = board.get(i + 1, j + 1);
            }
        }
    }



    /** Return the number of rows and of columns of THIS. */
    @Override
    int size() {
        return _size;
    }


    /** Returns the contents of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    @Override
    Square get(int n) {
        int r = row(n);
        int c = col(n);
        return this._mutableBoard[r - 1][c - 1];
    }


    /** Return the number of squares of given SIDE. */
    @Override
    int numOfSide(Side side) {
        int count = 0;
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.size(); j++) {
                if (this._mutableBoard[i][j].getSide().equals(side)) {
                    count += 1;
                }
            }
        }
        return count;
    }


    /** Returns the total number of spots on the board. */
    @Override
    int numPieces() {
        int total = 0;
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.size(); j++) {
                total += this._mutableBoard[i][j].getSpots();
            }
        }
        return total;
    }

    @Override
    void addSpot(Side player, int r, int c) {
        markUndo();
        helperJump(player, r, c);

        announce();
    }

    /** A helper to Add stuff including PLAYER, R, C. */
    void helperJump(Side player, int r, int c) {
        if (this.getWinner() != null) {
            return;
        }

        Square current = this._mutableBoard[r - 1][c - 1];
        Side currentColor = current.getSide();
        int currentSpot = current.getSpots();
        Square newToAdd = square(player, currentSpot + 1);
        this._mutableBoard[r - 1][c - 1] = newToAdd;
        if (newToAdd.getSpots() > neighbors(r, c)) {
            helperHelper(player, r , c);
        }
    }

    /** A helper to jump stuff including PLAYER, R, C. */
    void helperHelper(Side player, int r, int c) {
        Square now = this._mutableBoard[r - 1][c - 1];
        Side nowColor = now.getSide();
        int nowSpot = now.getSpots();

        if (r == 1 && c == 1) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 2);
            helperJump(player, r + 1, c);
            helperJump(player, r, c + 1);
        } else if (r == 1 && c == this.size()) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 2);
            helperJump(player, r + 1, c);
            helperJump(player, r, c - 1);
        } else if (r == this.size() && c == 1) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 2);
            helperJump(player, r - 1, c);
            helperJump(player, r, c + 1);
        } else if (r == this.size() && c == this.size()) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 2);
            helperJump(player, r - 1, c);
            helperJump(player, r, c - 1);
        } else if (r == 1) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 3);
            helperJump(player, r, c - 1);
            helperJump(player, r, c + 1);
            helperJump(player, r + 1, c);
        } else if (r == this.size()) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 3);
            helperJump(player, r, c - 1);
            helperJump(player, r, c + 1);
            helperJump(player, r - 1, c);
        } else if (c == 1) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 3);
            helperJump(player, r - 1, c);
            helperJump(player, r + 1, c);
            helperJump(player, r, c + 1);
        } else if (c == this.size()) {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 3);
            helperJump(player, r - 1, c);
            helperJump(player, r + 1, c);
            helperJump(player, r, c - 1);
        } else {
            this._mutableBoard[r - 1][c - 1] = square(nowColor, nowSpot - 4);
            helperJump(player, r - 1, c);
            helperJump(player, r + 1, c);
            helperJump(player, r, c - 1);
            helperJump(player, r, c + 1);
        }
    }


    @Override
    void addSpot(Side player, int n) {
        int r = row(n);
        int c = col(n);
        addSpot(player, r, c);
        announce();
    }

    @Override
    void set(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), square(player, num));
    }

    @Override
    void set(int n, int num, Side player) {
        internalSet(n, square(player, num));
        announce();
    }

    @Override
    void undo() {
        this._mutableBoard = _history.pop();

    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        MutableBoard history = new MutableBoard(this);

        _history.push(history._mutableBoard);
    }

    /** Set the contents of the square with index IND to SQ. Update counts
     *  of numbers of squares of each color.  */
    private void internalSet(int ind, Square sq) {
        int r = row(ind);
        int c = col(ind);
        this._mutableBoard[r - 1][c - 1] = sq;
    }

    /** Notify all Observers of a change. */
    private void announce() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoard)) {
            return obj.equals(this);
        } else {
            MutableBoard objjj = (MutableBoard) obj;
            for (int i = 0; i < this.size(); i++) {
                for (int j = 0; j < this.size(); j++) {
                    Square me = this._mutableBoard[i][j];
                    Square that = objjj._mutableBoard[i][j];
                    if (!me.equals(that)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int hashResult = 0;
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.size(); j++) {
                hashResult += _mutableBoard[i][j].hashCode();
            }
        }
        return 0;
    }

    /** _MUTABLEBOARD. */
    private Square[][] _mutableBoard;

    /** _SIZE. */
    private int _size;

    /** _MOVES. */
    private int _moves;

    /** _HISTORY. */
    private Stack<Square[][]> _history;

}
