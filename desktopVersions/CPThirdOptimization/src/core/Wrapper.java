// package core;
//
// import game_objects.BlackDama;
// import game_objects.BlackPawn;
// import game_objects.Cell;
// import game_objects.ChessboardImpl;
// import game_objects.ChessboardItem;
// import game_objects.EmptyTile;
// import game_objects.Move;
// import game_objects.PawnsColor;
// import game_objects.WhiteDama;
// import game_objects.WhitePawn;
// import prompters.ASPCheckersPrompter;
// import prompters.AbstractPrompter;
// import unipd.Board;
// import unipd.Checkers;
//
// public class Wrapper extends AbstractPrompter {
//
// private ChessboardImpl chessboard;
// private PawnsColor playerColor;
// private ASPCheckersPrompter prompter;
//
// public Wrapper(int[][] board, int color) {
// this.chessboard = new ChessboardImpl();
// createChessboardFromFile(board);
// this.playerColor = decodePlayerColorID(color);
// }
//
// @Override
// public void solve() {
// this.prompter = new ASPCheckersPrompter(chessboard, playerColor);
// prompter.solve();
// }
//
// @Override
// public Object getSolution() {
// Move m = this.prompter.getSolution();
// Board.MyMove[] myMoves = convertSolution(m);
// return myMoves;
// }
//
// private Board.MyMove[] convertSolution(Move m) {
//
// if (m == null)
// return null;
//
// Cell[] steps = new Cell[m.getMoveSteps().size()];
// m.getMoveSteps().toArray(steps);
// Board.MyMove[] myMoves = new Board.MyMove[steps.length - 1];
//
// for (int i = 0; i < steps.length - 1; i++) {
// Cell src = steps[i];
// Cell dst = steps[i + 1];
// myMoves[i] = new Board.MyMove(src.getRow(), src.getCol(), dst.getRow(),
// dst.getCol());
// }
// return myMoves;
// }
//
// private void createChessboardFromFile(int[][] board) {
// for (int i = 0; i < board.length; i++)
// for (int j = 0; j < board[i].length; j++)
// this.chessboard.setCell(i, j, convertNumberInItem(board[i][j], i, j));
// }
//
// private ChessboardItem convertNumberInItem(int idItem, int i, int j) {
// ChessboardItem item;
// switch (idItem) {
// case Checkers.BLACK:
// item = new WhitePawn(i, j);
// break;
// case Checkers.WHITE:
// item = new BlackPawn(i, j);
// break;
// case Checkers.BKING:
// item = new WhiteDama(i, j);
// break;
// case Checkers.WKING:
// item = new BlackDama(i, j);
// break;
// case Checkers.EMPTY:
// item = new EmptyTile(i, j);
// break;
// default:
// throw new RuntimeException("id not supported");
// }
// return item;
// }
//
// private PawnsColor decodePlayerColorID(int color) {
// PawnsColor p;
// switch (color) {
// case Checkers.BLACK:
// p = PawnsColor.WHITE;
// break;
// case Checkers.WHITE:
// p = PawnsColor.BLACK;
// break;
// default:
// throw new RuntimeException("unsopported color id");
// }
// return p;
// }
//
// }
