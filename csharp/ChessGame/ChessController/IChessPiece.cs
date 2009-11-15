namespace ChessController
{
    internal interface IChessPiece
    {
        void Attach(IChessBoard board);
        void Detach();
        bool ValidateMove(Vector direction);
        ChessColor Color {set; get;}
        bool OnStartPosition {set;get;}
    }
}
