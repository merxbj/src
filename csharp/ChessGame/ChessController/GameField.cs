namespace ChessController
{
    internal class GameField : IField
    {
        private Position pos;
        private ChessColor color;

        public Position Pos
        {
            get { return pos; }
            set { pos = value; }
        }

        public ChessColor Color
        {
            get { return color; }
            set { color = value; }
        }

        public override string ToString()
        {
            return Pos.ToString();
        }

        public override bool Equals(object obj)
        {
            return Pos.Equals(obj);
        }

        public override int GetHashCode()
        {
            return Pos.GetHashCode();
        }
    }
}
