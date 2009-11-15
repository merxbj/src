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
            if ((direction.CompareTo(new Vector(0, yDir)) == 0) ||
                (OnStartPosition && direction.CompareTo(new Vector(0, yDir * 2)) == 0))
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
            else if (direction.CompareTo(new Vector( 1, yDir)) == 0 || 
                     direction.CompareTo(new Vector(-1, yDir)) == 0)
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
            if (direction.CompareTo(new Vector( 1, 2)) == 0 ||
                direction.CompareTo(new Vector( 2, 1)) == 0 ||
                direction.CompareTo(new Vector(-1, 2)) == 0 ||
                direction.CompareTo(new Vector(-2, 1)) == 0 ||
                direction.CompareTo(new Vector( 1,-2)) == 0 ||
                direction.CompareTo(new Vector( 2,-1)) == 0 ||
                direction.CompareTo(new Vector(-1,-2)) == 0 ||
                direction.CompareTo(new Vector(-2,-1)) == 0)
            {
                return true;
            }

            return false;
        }
    }

    internal class Bishop : ChessPiece
    {
        public override bool ValidateMove(Vector direction)
        {
            return (Math.Abs(direction.x) == Math.Abs(direction.y));
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
            bool isCastling = (OnStartPosition && (direction.y == 0) && (Math.Abs(direction.x).CompareTo(2) == 0));
            
            if ((Math.Abs(direction.Size).CompareTo(1) != 0) &&
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
