using System;

namespace ChessController
{
    class Vector : IComparable
    {
        public Vector(int x, int y)
        {
            this.x = x;
            this.y = y;
        }


        public Vector()
        {
        }

        public int CompareTo(object obj)
        {
            if (!(obj is Vector))
                throw new ArgumentException("Object is not a Vector class type!");

            Vector vec = (Vector) obj;

            return (int)(Math.Ceiling(Size - vec.Size));
        }


        public override bool Equals(object obj)
        {
            if (!(obj is Vector))
                throw new ArgumentException("Object is not a Vector class type!");

            Vector vec = (Vector) obj;

            return (x == vec.x) && (y == vec.y);
        }

        public Vector Direction
        {
            get { return new Vector(x / Math.Max(Math.Abs(x), 1), y / Math.Max(Math.Abs(y), 1));}
        }

        public double Size
        {
            get { return Math.Sqrt(Math.Pow(x, 2) + Math.Pow(y, 2)); }
        }

        public int x;
        public int y;
    }
}
