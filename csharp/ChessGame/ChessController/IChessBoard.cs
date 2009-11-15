using System;
using System.Collections.Generic;
using System.Text;

namespace ChessController
{
    internal interface IChessBoard
    {
        IChessPiece GetFieldPiece(Position pos);
        Position GetPiecePosition(IChessPiece piece);
        void Wipe();
        List<IChessPiece> GetFieldAttackers(Position pos);
    }
}
