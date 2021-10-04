using System.Text;

namespace ElectionVisualiser
{
    class Program
    {
        static void Main(string[] args)
        {
            Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);

            var downloader = new Election2006DataDownloader();
            var resultsXml = downloader.DownloadResults();
            var partiesXml = downloader.DownloadParties();
            var candidatesXml = downloader.DownloadCandidates();
            var partyDataProvider = new PartyDataProvider(partiesXml, candidatesXml);
            var analyzer = new ElectionResultsAnalyzer(resultsXml, partyDataProvider);
            var results = analyzer.CalculateResults();
        }
    }
}
