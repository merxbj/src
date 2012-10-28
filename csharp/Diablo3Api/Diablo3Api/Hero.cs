using System;
using System.Text;
using Newtonsoft.Json;

namespace Diablo3Api
{
    class Hero
    {
        public string Name { get; set; }
        public int Id { get; set; }
        public int Level { get; set; }
        public bool Hardcore { get; set; }
        public int ParagonLevel { get; set; }
        public int Gender { get; set; }
        public bool Dead { get; set; }
        public string Class { get; set; }
        [JsonConverter(typeof(UnixDateTimeConverter))]
        [JsonProperty("last-updated")]
        public DateTime LastUpdated { get; set; }

        public override string ToString()
        {
            StringBuilder builder = new StringBuilder();
            builder.Append(Id).Append(",");
            builder.Append(Name).Append(",");
            builder.Append(Level).Append(",");
            builder.Append(Hardcore).Append(",");
            builder.Append(ParagonLevel).Append(",");
            builder.Append(Gender).Append(",");
            builder.Append(Dead).Append(",");
            builder.Append(Class).Append(",");
            builder.Append(LastUpdated).Append(",");
            return builder.ToString();
        }
    }
}