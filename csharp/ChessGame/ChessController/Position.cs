using System;

namespace ChessController
{
    class Position : IComparable
    {
        public Position(int x, int y)
        {
            X = x;
            Y = y;
        }


        public Position()
        {
        }

        public int CompareTo(object obj)
        {
            if (!(obj is Position))
                throw new ArgumentException("Object is not a Position class type!");

            Position pos = (Position)obj;

            if ((pos.X == X) && (pos.Y == Y))
                return 0;
            else
                return (int)(Math.Ceiling(Size - pos.Size));
        }

        public int X
        {
            get { return x; }
            set
            {
                if (value <= 0)
                    throw new ArgumentException("Position coordinate must be greater than zero!");
                x = value;
            }
        }

        public int Y
        {
            get { return y; }
            set
            {
                if (value <= 0)
                    throw new ArgumentException("Position coordinate must be greater than zero!");
                y = value;
            }
        }

        public override bool Equals(object obj)
        {
            if (!(obj is Position))
                throw new ArgumentException("Object is not a Position class type!");

            Position pos = (Position) obj;

            return (CompareTo(pos) == 0);
        }

        public override string ToString()
        {
            return string.Format("({0},{1})", X, Y);
        }

        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }

        public Position Move(Vector vec)
        {
            return new Position(X + vec.x, Y + vec.y);
        }

        /// <summary>
        /// Represents a distance from the origin (0,0)
        /// </summary>
        public double Size { get { return Math.Sqrt(Math.Pow(x, 2) + Math.Pow(y, 2)); } }

        private int x;
        private int y;
    }
}
