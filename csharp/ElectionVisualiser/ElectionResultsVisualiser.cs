using System;
using System.Collections.Generic;

namespace ElectionVisualiser
{
    class ElectionResultsVisualiser
    {
        public void Visualise(LinkedList<TotalResults> lastTwoResults, TimeSpan timeToNextUpdate)
        {
            var currentResults = lastTwoResults.First.Value;
            var previousResults = lastTwoResults.Last.Value;

            string areaCompletedDiff = string.Format("{0:+##.##;-##.##;+0.00}", currentResults.AreasCompleted - previousResults.AreasCompleted);
            string participationDiff = string.Format("{0:+##.##;-##.##;+0.00}", currentResults.Participation - previousResults.Participation);

            Console.Clear();

            Console.WriteLine($"      Timestamp: {currentResults.Timestamp}");
            Console.WriteLine($"Areas Completed: {currentResults.AreasCompleted}\t({areaCompletedDiff})");
            Console.WriteLine($"  Participation: {currentResults.Participation}\t({participationDiff})");
            Console.WriteLine();
            Console.WriteLine();

            if (currentResults.AreasCompleted < 5m)
            {
                Console.WriteLine("Not enough votes collected, yet ...");
            }
            else
            {
                VisualisePartyResults(currentResults.PartyResults.Values, previousResults.PartyResults);
            }

            Console.WriteLine();
            Console.WriteLine();

            Console.WriteLine($"Time to next update: {timeToNextUpdate:ss}");
        }

        private void VisualisePartyResults(ICollection<PartyResults> currentResults, IDictionary<Party, PartyResults> previousResults)
        {
            var sortedParties = new List<PartyResults>();
            sortedParties.AddRange(currentResults);

            sortedParties.Sort((left, right) => {
                return right.Mandates.CompareTo(left.Mandates);
            });


            int maxNameLength = int.MinValue;
            foreach (var partyResults in sortedParties)
            {
                if (partyResults.Party.Name.Length > maxNameLength)
                {
                    maxNameLength = partyResults.Party.Name.Length;
                }
            }

            int mandatesSum = 0;
            foreach (var partyResults in sortedParties)
            {
                string partyName = partyResults.Party.Name.PadLeft(maxNameLength);
                string mandates = partyResults.Mandates.ToString().PadLeft(3);
                string mandatesDiff = string.Format("{0:+###;-###;+0}", partyResults.Mandates - previousResults[partyResults.Party].Mandates);

                Console.WriteLine($"{partyName}: {mandates} ({mandatesDiff})");
                mandatesSum += partyResults.Mandates;
            }

            Console.WriteLine();
            Console.WriteLine($"{"Total Check Sum".PadLeft(maxNameLength)}: {mandatesSum.ToString().PadLeft(3)}");
        }
    }
}