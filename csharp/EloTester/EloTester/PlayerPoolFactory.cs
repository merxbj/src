using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EloTester
{
    public class PlayerPoolFactory
    {
        public PlayerPoolFactory(Stream nameListStream, Random rand)
        {
            this.rand = rand;
            names = new List<String>();
            using (TextReader reader = new StreamReader(nameListStream))
            {
                String name = reader.ReadLine();
                while (!String.IsNullOrEmpty(name))
                {
                    names.Add(name);
                    name = reader.ReadLine();
                }
            }
        }

        public List<Player> CreatePlayerPool(int poolSize, int eloMin, int eloMax)
        {
            List<Player> pool = new List<Player>(poolSize);
            
            for (int i = 0; i < poolSize; i++)
            {
                int elo = rand.Next(eloMin, eloMax);
                string name = String.Empty;
                Dictionary<String, bool> usage = new Dictionary<String, bool>();
                do {
                    int nameIndex = rand.Next(names.Count());
                    name = names[nameIndex];
                } while (usage.ContainsKey(name));
                usage[name] = true;

                Player p = new Player(name, elo)
                {
                    Rand = rand
                };
                pool.Add(p);
            }

            return pool;
        }

        private List<String> names;
        private Random rand;
    }
}
