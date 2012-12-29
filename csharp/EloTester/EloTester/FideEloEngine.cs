using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EloTester
{
    public class FideEloEngine : IEloEngine
    {
        public FideEloEngine()
        {
            reachedTenFactor = new Dictionary<Player, bool>();
        }

        public void EvaluateResult(Player one, Player two, double score)
        {
            double esOne = CalculateExpectedScore(one, two);
            double esTwo = CalculateExpectedScore(two, one);
            double addOne = KFactor(one) * (score - esOne);
            double addTwo = KFactor(two) * ((1 - score) - esTwo);
            one.CalculatedElo = Math.Max(MinRating, (int)(Math.Floor(one.CalculatedElo + addOne)));
            two.CalculatedElo = Math.Max(MinRating, (int)(Math.Floor(two.CalculatedElo + addTwo)));

        }

        public void AssignInitialRating(Player p)
        {
            p.CalculatedElo = 1500;
        }

        public double CalculateExpectedScore(Player one, Player two)
        {
            return CalculateExpectedScore(one.CalculatedElo, two.CalculatedElo);
        }

        public double CalculateExpectedScore(int eloOne, int eloTwo)
        {
            return 1 / (1 + Math.Pow(10, (eloTwo - eloOne) / 400));
        }

        public double KFactor(Player p)
        {
            if (p.NumGames < 30)
            {
                return 30;
            }
            
            if (reachedTenFactor.ContainsKey(p))
            {
                return 10;
            }

            if (p.CalculatedElo > 2400)
            {
                reachedTenFactor[p] = true;
                return 10;
            }

            return 15;
        }

        public int MinRating
        {
            get { return 1200; }
        }

        public int MaxRating
        {
            get { return 3000; }
        }

        private Dictionary<Player, bool> reachedTenFactor;
    }
}
