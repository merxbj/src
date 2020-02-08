using System;
using System.Collections.Generic;
using System.Text;

namespace ChessController
{
    internal class ChessBoard : IChessBoard
    {
        #region Public properties

        public int Width { get { return width; } }
        public int Height { get { return height; } }

        #endregion

        #region Private members

        //private 
        
        private const int width = 8;
        private const int height = 8;

        #endregion

        #region IChessBoard Members

        public IChessPiece GetFieldPiece(Position pos)
        {
            throw new NotImplementedException();
        }

        public Position GetPiecePosition(IChessPiece piece)
        {
            throw new NotImplementedException();
        }

        public void Wipe()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public List<IChessPiece> GetFieldAttackers(Position pos)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}
