using System;
using System.Collections.Generic;
using System.Text;

namespace ChessController
{
    interface IChessGame
    {
        void BuildDefaultSetup();
        void CreateCustomPiece(ChessConstants type, int x, int y);
        void MovePiece(int start_x, int start_y, int dest_x, int dest_y);
        void TransformPiece(ChessConstants type, int x, int y);
        void BeginGame();
        void EndGame();
    }
}
