using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using NcrOrgChartDataGrabber;

namespace NcrOrgChartDataGrabberApp
{
    class Application
    {
        static void Main(string[] args)
        {
            if (args[0] == "walk")
            {
                Walker walker = new Walker(Console.Out);
                Employee manager = walker.WalkOrganizationDownFrom(args[1]);
                SerializeObject(manager, args[1] + ".dat");
            }
            else if (args[0] == "load")
            {
                Employee manager = DeSerializeObject<Employee>(args[1] + ".dat");
                WalkOrganizationDownFrom(manager, 0);
            }

            Console.WriteLine("Done.");
        }

        private static void WalkOrganizationDownFrom(Employee employee, int level)
        {
            for (int i = 0; i < level; i++)
            {
                Console.Write(" ");
            }

            Console.WriteLine(String.Format("{0}:\t{1}", level, employee));
            if (employee.DirectReports != null)
            {
                foreach (Employee directReport in employee.DirectReports)
                {
                    WalkOrganizationDownFrom(directReport, level + 1);
                }
            }
        }

        public static void SerializeObject<T>(T objectToSerialize, string filename)
        {
            Stream stream = File.Open(filename, FileMode.Create);
            BinaryFormatter formatter = new BinaryFormatter();
            formatter.Serialize(stream, objectToSerialize);
            stream.Close();
        }

        public static T DeSerializeObject<T>(string filename)
        {
            Stream stream = File.Open(filename, FileMode.Open);
            BinaryFormatter formatter = new BinaryFormatter();
            T objectToSerialize = (T)formatter.Deserialize(stream);
            stream.Close();
            return objectToSerialize;
        }
    }
}
