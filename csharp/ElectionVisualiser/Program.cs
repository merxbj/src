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
                //PlayLiveResults();
                RePlayCapturedResults();
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.ToString());
            }
        }

        static void PlayLiveResults()
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

            while (results.Count == 0 || results.First.Value.AreasCompleted < 100.0m)
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

            Console.Out.WriteLine("");
            Console.Out.WriteLine("All votes have been counted.");
        }

        static void RePlayCapturedResults()
        {
            Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);

            var downloader = new Election2021DataSnapshotDownloader();
            var partiesXml = downloader.DownloadParties();
            var candidatesXml = downloader.DownloadCandidates();
            var partyDataProvider = new PartyDataProvider(partiesXml, candidatesXml);
            var analyzer = new ElectionResultsAnalyzer(partyDataProvider);
            var visualiser = new ElectionResultsVisualiser();
            
            var results = new LinkedList<TotalResults>();

            foreach (var resultsXml in downloader.EnumerateParties("/Users/jaroslavlek/data"))
            {
                try
                {
                    results.AddFirst(analyzer.CalculateResults(resultsXml));
                    if (results.Count > 2)
                    {
                        results.RemoveLast();
                    }
                    
                    visualiser.Visualise(results, DataReplayPeriod);

                    Thread.Sleep(DataReplayPeriod);
                }
                catch (Exception ex)
                {
                    Console.Error.WriteLine(ex.ToString());
                }
            }

            Console.Out.WriteLine("");
            Console.Out.WriteLine("All votes have been counted.");
        }


        static readonly TimeSpan DataRefreshPeriod = TimeSpan.FromSeconds(60);
        static readonly TimeSpan DataReplayPeriod = TimeSpan.FromMilliseconds(250);
    }
}
