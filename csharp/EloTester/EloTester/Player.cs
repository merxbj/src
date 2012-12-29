using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EloTester
{
    public class Player : IComparable<Player>, IEquatable<Player>
    {
        public Player(string name, int newRealElo)
        {
            RealElo = newRealElo;
            Name = name;
        }

        public double Play(Player other, IEloEngine elo)
        {
            double expectedScore = elo.CalculateExpectedScore(RealElo, other.RealElo); // real expected result
            double score = evaluateExpectedScore(expectedScore, false);
            this.NumGames++;
            other.NumGames++;
            return score;
        }

        public int CompareTo(Player other)
        {
            return this.Name.CompareTo(other.Name);
        }

        public bool Equals(Player other)
        {
            return this.Name.Equals(other.Name);
        }

        public override bool Equals(object obj)
        {
            // If parameter cannot be cast to ThreeDPoint return false:
            Player other = obj as Player;
            if ((object)other == null)
            {
                return false;
            }

            // Return true if the fields match:
            return base.Equals(obj) && this.Name.Equals(other.Name);
        }

        public override int GetHashCode()
        {
            return this.Name.GetHashCode();
        }

        private double evaluateExpectedScore(double expectedScore, bool drawAllowed)
        {
            if (expectedScore > 0.75)
            {
                return 1;
            }
            else if (expectedScore < 0.25)
            {
                return 0;
            }
            else if (!drawAllowed)
            {
                double rnd = Rand.NextDouble() * 0.5;
                return evaluateExpectedScore((expectedScore > 0.5) ? expectedScore + rnd : expectedScore - rnd, true);
            }
            else
            {
                return 0.5;
            }
        }

        public int RealElo { get; set; }
        public int CalculatedElo { get; set; }
        public string Name { get; set; }
        public int NumGames { get; set; }
        public Random Rand { get; set; }
    }
}
