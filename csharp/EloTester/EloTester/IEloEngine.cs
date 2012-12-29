using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EloTester
{
    public interface IEloEngine
    {
        void AssignInitialRating(Player p);
        double CalculateExpectedScore(Player one, Player two);
        double CalculateExpectedScore(int eloOne, int eloTwo);
        void EvaluateResult(Player one, Player two, double score);
        double KFactor(Player p);
        int MinRating {get;}
        int MaxRating {get;}
    }
}
