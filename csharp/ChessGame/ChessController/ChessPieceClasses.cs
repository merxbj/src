using System;
using System.Collections.Generic;

namespace ChessController
{
    internal class Pawn : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            int yDir = (Color == ChessColor.Black) ? -1 : 1;
            
            // Move
            if ((direction.Equals(new Vector(0, yDir))) ||
                (OnStartPosition && direction.Equals(new Vector(0, yDir * 2))))
            {
                Position pos = GetPositionOnBoard().Move(direction);
                IChessPiece piece = Board.GetFieldPiece(pos);
                if (piece == null)
                {
                    if (Math.Abs(direction.y) > 1)
                    {
                        pos = pos.Move(new Vector(0, -yDir));
                        piece = Board.GetFieldPiece(pos);
                        if (piece == null)
                            return true;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
            // Attacking
            else if (direction.Equals(new Vector( 1, yDir)) || 
                     direction.Equals(new Vector(-1, yDir)))
            {
                Position pos = GetPositionOnBoard();
                IChessPiece enemy = Board.GetFieldPiece(pos.Move(direction));

                if ((enemy != null) && (enemy.Color != Color))
                    return true;
                // En Passant
                else if (EnPassantPossible)
                    return true;
            }

            return false;
        }

        public bool EnPassantPossible
        {
            get { return enPassantPossible; }
            set { enPassantPossible = value; }
        }

        private bool enPassantPossible;
    }

    internal class Knight : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            if (direction.Equals(new Vector( 1,  2)) ||
                direction.Equals(new Vector( 2,  1)) ||
                direction.Equals(new Vector(-1,  2)) ||
                direction.Equals(new Vector(-2,  1)) ||
                direction.Equals(new Vector( 1, -2)) ||
                direction.Equals(new Vector( 2, -1)) ||
                direction.Equals(new Vector(-1, -2)) ||
                direction.Equals(new Vector(-2, -1)))
            {
                Position pos = GetPositionOnBoard().Move(direction);
                IChessPiece piece = Board.GetFieldPiece(pos);
                if ((piece == null) || (!Color.Equals(piece.Color)))
                    return true;
            }

            return false;
        }
    }

    internal class Bishop : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            if (Math.Abs(direction.x) == Math.Abs(direction.y))
            {
                Vector simpleDirection = direction.Direction;
                Position startPos = GetPositionOnBoard();
                Position endPos = startPos.Move(direction);
                Position pos = startPos.Move(simpleDirection);
                IChessPiece piece;

                while (!pos.Equals(endPos))
                {
                    piece = Board.GetFieldPiece(pos);
                    if (piece != null)
                        return false; // there is a piece in our way!

                    pos.Move(simpleDirection);
                }

                piece = Board.GetFieldPiece(endPos);
                if (piece != null && piece.Color == Color)
                    return false;

                return true;
            }

            return false;
        }
    }

    internal class Rook : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            return (direction.x == 0) || (direction.y == 0);
        }
    }

    internal class Queen : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            return (Math.Abs(direction.x) == Math.Abs(direction.y)) || ((direction.x == 0) || (direction.y == 0));
        }
    }

    internal class King : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            bool isCastling = (OnStartPosition && (direction.y == 0) && (Math.Abs(direction.x).Equals(2)));
            
            if (!(Math.Abs(direction.Size).Equals(1)) &&
                !isCastling)
            {
                return false;
            }

            Position pos = GetPositionOnBoard().Move(direction);
            List<IChessPiece> attackers = Board.GetFieldAttackers(pos);
            if (attackers != null)
            {
                foreach (IChessPiece piece in attackers)
                {
                    if (piece.Color != Color)
                        return false;
                }
                
                attackers.Clear();
            }

            if (isCastling)
            {
                if (direction.x > 0)
                    pos = pos.Move(new Vector(-1, 0));
                else
                    pos = pos.Move(new Vector(1, 0));


                attackers = Board.GetFieldAttackers(pos);
                if (attackers != null)
                {
                    foreach (IChessPiece piece in attackers)
                    {
                        if (piece.Color != Color)
                            return false;
                    }
                }
            }

            return true;
        }
    }
}
