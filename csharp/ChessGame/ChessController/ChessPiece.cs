using System;

namespace ChessController
{
    internal class ChessPiece : IChessPiece
    {
        private ChessColor color;
        private bool onStartPosition;

        #region IChessPiece Members

        public void Attach(IChessBoard board)
        {
            this.board = board;
        }

        public void Detach()
        {
            board = null;
        }

        public virtual bool ValidateMove(Vector direction)
        {
            throw new Exception("Not a real chess piece!");
        }

        public ChessColor Color
        {
            get { return color; }
            set { color = value; }
        }

        public bool OnStartPosition
        {
            get { return onStartPosition; }
            set { onStartPosition = value; }
        }

        #endregion

        #region Helper methods

        protected Position GetPositionOnBoard()
        {
            if (board == null)
                throw new Exception("Not on any board!");
            else
                return board.GetPiecePosition(this);
        }

        protected  bool IsWayClear()
        {
            
            return false;
        }

        #endregion

        public IChessBoard Board
        {
            get { return board; }
        }

        #region Private members

        private IChessBoard board;

        #endregion
    }
}
