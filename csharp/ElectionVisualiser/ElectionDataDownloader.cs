using System.IO;
using System.Net;
using System.Text;
using System.Xml;

namespace ElectionVisualiser
{
    interface IElectionDataDownloader
    {
        XmlDocument DownloadParties();
        XmlDocument DownloadResults();
        XmlDocument DownloadCandidates();
    }

    class Election2017DataDownloader : IElectionDataDownloader
    {
        public XmlDocument DownloadResults()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/pls/ps2017nss/vysledky"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public XmlDocument DownloadParties()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2017reg20171122/psrkl.xml", Encoding.GetEncoding(1250)))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }

        public XmlDocument DownloadCandidates()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2017reg20171122/psrk.xml", Encoding.GetEncoding(1250)))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }
    }

    class Election2021DataDownloader : IElectionDataDownloader
    {
        public XmlDocument DownloadResults()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://www.volby.cz/pls/ps2021/vysledky"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public XmlDocument DownloadParties()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/opendata/ps2021/xml/psrkl.xml"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public XmlDocument DownloadCandidates()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/opendata/ps2021/xml/psrk.xml"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }
    }

    class Election2006DataDownloader : IElectionDataDownloader
    {
        public XmlDocument DownloadResults()
        {
            XmlDocument doc = new XmlDocument();
            using (XmlTextReader tr = new XmlTextReader("https://volby.cz/pls/ps2006/vysledky"))
            {
                tr.Namespaces = false;
                doc.Load(tr);
            }
            return doc;
        }

        public XmlDocument DownloadParties()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2006reg2006/psrkl.xml"))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }

        public XmlDocument DownloadCandidates()
        {
            XmlDocument doc = new XmlDocument();
            using (StreamReader sr = new StreamReader("/users/jaroslavlek/Downloads/PS2006reg2006/psrk.xml"))
            {
                using (XmlTextReader tr = new XmlTextReader(sr))
                {
                    tr.Namespaces = false;
                    doc.Load(tr);
                }
            }

            return doc;
        }
    }
}