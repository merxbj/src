using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EloTester
{
    class Application
    {
        static void Main(string[] args)
        {
            Random rand = new Random();
            IEloEngine elo = new FideEloEngine();
            PlayerPoolFactory ppf = new PlayerPoolFactory(new FileStream(@"d:\temp\names\names.txt", FileMode.Open), rand);
            List<Player> pool = ppf.CreatePlayerPool(1000, elo.MinRating, elo.MaxRating);
            Ladder ladder = new Ladder(pool, elo);
            /*ladder.OnStartGame += player_OnStartGame;
            ladder.OnEndGame += player_OnEndGame;
            ladder.RegisterForTracking(pool[rand.Next(0, pool.Count())]);*/

            ladder.Compete(10000);

            evaluateEloAccuracty(pool);
            Console.ReadLine();
        }

        static void player_OnEndGame(Player one, Player two, double score)
        {
            Console.WriteLine("Result: {0}, {1} new Calculated = {2}, {3} new Calculated = {4}",
                score,
                one.Name, one.CalculatedElo,
                two.Name, two.CalculatedElo);
            Console.ReadLine();
        }

        static void player_OnStartGame(Player one, Player two)
        {
            Console.WriteLine("{0}(Real = {1}, Calculated = {2}) vs {3}(Real = {4}, Calculated = {5})",
                one.Name, one.RealElo, one.CalculatedElo,
                two.Name, two.RealElo, two.CalculatedElo);
        }

        private static void evaluateEloAccuracty(List<Player> pool)
        {
            int inaccuratePlayers = 0;
            foreach (Player p in pool)
            {
                // if over 10% inaccuracy
                if (Math.Min(p.RealElo, p.CalculatedElo) / Math.Max(p.RealElo, p.CalculatedElo) < 0.1)
                {
                    Console.WriteLine("Inaccurate ELO for {0}. Real = {1}, Calculated = {2} after playing {3} games.", p.Name, p.RealElo, p.CalculatedElo, p.NumGames);
                    inaccuratePlayers++;
                }
            }
            Console.WriteLine("Inaccurate ELO calculated for {0} players out of {1}.", inaccuratePlayers, pool.Count());
        }
    }
}
