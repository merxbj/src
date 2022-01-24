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
                VisualisePartyResults(currentResults.PartyResults, previousResults.PartyResults);
            }

            Console.WriteLine();
            Console.WriteLine();

            Console.WriteLine($"Time to next update: {timeToNextUpdate:ss}");
        }

        private void VisualisePartyResults(IDictionary<Party, PartyResults> currentResults, IDictionary<Party, PartyResults> previousResults)
        {
            var sortedParties = new List<PartyResults>();
            sortedParties.AddRange(currentResults.Values);

            sortedParties.Sort((left, right) => {
                int result = right.Mandates.CompareTo(left.Mandates);
                if (result == 0)
                {
                    result = right.Percentage.CompareTo(left.Percentage);
                }
                return result;
            });


            int maxNameLength = int.MinValue;
            foreach (var partyResults in sortedParties)
            {
                if (partyResults.Percentage > 0.5m)
                {
                    if (partyResults.Party.Name.Length > maxNameLength)
                    {
                        maxNameLength = partyResults.Party.Name.Length;
                    }
                }
            }

            int mandatesSum = 0;
            foreach (var partyResults in sortedParties)
            {
                if (partyResults.Percentage > 0.5m)
                {
                    string partyName = partyResults.Party.Name.PadLeft(maxNameLength);
                    string mandates = partyResults.Mandates.ToString().PadLeft(3);
                    string mandatesDiff = string.Format("{0:+###;-###;+0}", partyResults.Mandates - previousResults[partyResults.Party].Mandates);
                    string percent = partyResults.Percentage.ToString().PadLeft(6);
                    string percentDiff = string.Format("{0:+#.##;-#.##;+0.00}", partyResults.Percentage - previousResults[partyResults.Party].Percentage);

                    Console.WriteLine($"{partyName}: {mandates} ({mandatesDiff}) | {percent} ({percentDiff})");
                    mandatesSum += partyResults.Mandates;
                }
            }

            Console.WriteLine();
            Console.WriteLine($"{"Total Check Sum".PadLeft(maxNameLength)}: {mandatesSum.ToString().PadLeft(3)}");
        }
    }
}