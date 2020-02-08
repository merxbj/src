using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Json;
using System.Text;

namespace NcrOrgChartDataGrabber
{
    public class Serialization
    {
        public static void SerializeObject<T>(T objectToSerialize, string filename)
        {
            using (var stream = File.Open(filename, FileMode.Create))
            {
                DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(T));
                ser.WriteObject(stream, objectToSerialize);
            }
        }

        public static T DeSerializeObject<T>(string filename)
        {
            using (Stream stream = File.Open(filename, FileMode.Open))
            {
                DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(T));
                return (T)ser.ReadObject(stream);
            }
        }
    }
}
