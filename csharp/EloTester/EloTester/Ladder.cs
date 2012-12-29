using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EloTester
{
    public class Ladder
    {
        public Ladder(List<Player> pool, IEloEngine elo)
        {
            this.pool = pool;
            this.elo = elo;
            this.tracked = new Dictionary<Player, bool>();
        }

        public void Compete(int numRounds)
        {
            pool.ForEach(player => elo.AssignInitialRating(player));

            for (int i = 0; i < numRounds; i++)
            {
                conductSingleRound();
            }
        }

        private void conductSingleRound()
        {
            pool.Sort(new CalculatedEloComparer());
            Queue<Player> queue = new Queue<Player>(pool);
            while (queue.Count() > 1)
            {
                Player p1 = queue.Dequeue();
                Player p2 = queue.Dequeue();
                TrackStartGame(p1, p2);
                double score = p1.Play(p2, elo);
                elo.EvaluateResult(p1, p2, score);
                TrackEndGame(p1, p2, score);
            }
        }

        private void TrackEndGame(Player p1, Player p2, double score)
        {
            if (tracked.ContainsKey(p1) || tracked.ContainsKey(p2))
            {
                OnEndGame(p1, p2, score);
            }
        }

        private void TrackStartGame(Player p1, Player p2)
        {
            if (tracked.ContainsKey(p1) || tracked.ContainsKey(p2))
            {
                OnStartGame(p1, p2);
            }
        }

        public void RegisterForTracking(Player p)
        {
            tracked[p] = true;
        }

        public event Action<Player, Player> OnStartGame = delegate { };
        public event Action<Player, Player, double> OnEndGame = delegate { };

        private List<Player> pool;
        private IEloEngine elo;
        private Dictionary<Player, bool> tracked;

        private class CalculatedEloComparer : IComparer<Player>
        {
            public int Compare(Player x, Player y)
            {
                return x.CalculatedElo.CompareTo(y.CalculatedElo);
            }
        }
    }
}
