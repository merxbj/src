using System;
using System.Collections.Generic;
using System.Text;

namespace ChessController
{
    public class ChessGame : IChessGame
    {
        public ChessGame()
        {
            board = new ChessBoard();
        }

        #region IChessController Members

        public void BuildDefaultSetup()
        {
            board.Wipe(); // clear the board first

        }

        public void CreateCustomPiece(ChessConstants type, int x, int y)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public void MovePiece(int start_x, int start_y, int dest_x, int dest_y)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public void TransformPiece(ChessConstants type, int x, int y)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public void BeginGame()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public void EndGame()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        #endregion

        #region Private methods

        #endregion

        #region Private members

        private IChessBoard board;

        #endregion

    }
}
