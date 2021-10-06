using System;
using System.Diagnostics;
using System.Text;
using System.Threading;
using System.Collections.Generic;

namespace ElectionVisualiser
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);

                var downloader = new Election2021DataDownloader();
                var partiesXml = downloader.DownloadParties();
                var candidatesXml = downloader.DownloadCandidates();
                var partyDataProvider = new PartyDataProvider(partiesXml, candidatesXml);
                var analyzer = new ElectionResultsAnalyzer(partyDataProvider);
                var visualiser = new ElectionResultsVisualiser();
                
                Stopwatch stopwatch = Stopwatch.StartNew();
                var results = new LinkedList<TotalResults>();

                while (true)
                {
                    try
                    {
                        if (results.Count == 0 || stopwatch.Elapsed >= DataRefreshPeriod)
                        {
                            var resultsXml = downloader.DownloadResults();
                            results.AddFirst(analyzer.CalculateResults(resultsXml));
                            if (results.Count > 2)
                            {
                                results.RemoveLast();
                            }
                            stopwatch.Restart();
                        }
                        
                        visualiser.Visualise(results, (DataRefreshPeriod - stopwatch.Elapsed));

                        Thread.Sleep(TimeSpan.FromSeconds(1));
                    }
                    catch (Exception ex)
                    {
                        if (results.Count > 1)
                        {
                            visualiser.Visualise(results, (DataRefreshPeriod - stopwatch.Elapsed));
                        }
                        
                        Console.Error.WriteLine(ex.ToString());
                    }
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.ToString());
            }
        }

        static readonly TimeSpan DataRefreshPeriod = TimeSpan.FromSeconds(60);
    }
}
