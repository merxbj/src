using System;
using System.IO;
using System.Net;
using Newtonsoft.Json;

namespace Diablo3Api
{
    class Program
    {
        static void Main(string[] args)
        {
            bool debug = false;
            foreach (string arg in args)
            {
                if (!String.IsNullOrEmpty(arg) && arg.Equals("debug"))
                {
                    debug = true;
                }
            }

            StringReader json = new StringReader("");
            if (debug)
            {
                using (FileStream file = new FileStream(@"d:\temp\profile.json", FileMode.Open))
                {
                    StreamReader reader = new StreamReader(file);
                    json = new StringReader(reader.ReadToEnd());
                }
            }
            else
            {
                HttpWebRequest hwr = (HttpWebRequest) WebRequest.Create("http://eu.battle.net/api/d3/profile/mrneo-2514/");
                hwr.Method = "GET";
                using (Stream response = hwr.GetResponse().GetResponseStream())
                {
                    if (response != null)
                    {
                        StreamReader reader = new StreamReader(response);
                        json = new StringReader(reader.ReadToEnd());
                    }
                }
            }
        
            JsonSerializer serializer = JsonSerializer.Create(null);
            Profile profile = serializer.Deserialize<Profile>(new JsonTextReader(json));
            if (profile != null)
            {
                DoStuff(profile);
            }

            Console.ReadLine();
        }

        private static void DoStuff(Profile profile)
        {
            foreach (Hero h in profile.Heroes)
            {
                Console.WriteLine(h);
            }
        }
    }
}
