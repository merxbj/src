using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IntegriIndexer.PublicNameIndexing
{
    class DatabasePublisher : IPublisher
    {
        public DatabasePublisher()
        {
        }

        public void Publish(List<Occurrence> occurrences)
        {
            using (SqlConnection con = new SqlConnection(conString))
            {
                con.Open();

                SqlCommand cleanUp = new SqlCommand("DELETE FROM Public_Object_Index", con);
                cleanUp.ExecuteNonQuery();

                int rowCount = 0;
                StringBuilder sql = new StringBuilder("");
                foreach (Occurrence o in occurrences)
                {
                    if (o.PublicObject.Type == ObjectType.Event)
                    {
                        if (rowCount == 1000)
                        {
                            Console.WriteLine("DatabasePublisher - Publishing {0} rows ...", rowCount);
                            sql.Insert(0, @"INSERT INTO Public_Object_Index VALUES ");
                            SqlCommand command = new SqlCommand(sql.ToString(0, sql.Length - 1), con);
                            command.ExecuteNonQuery();
                            sql.Clear();
                            rowCount = 0;
                        }

                        EventOccurence e = o as EventOccurence;
                        sql.AppendFormat("('{0}','{1}','{2}','{3}','{4}'),", 
                            o.PublicObject.MciFile, 
                            o.PublicObject.Type, 
                            o.PublicObject.Name, 
                            e.Project.Name,
                            e.ProgramName + " - " + (e.IsRaised ? "Raised" : "Handled"));

                        rowCount++;
                    }
                }

                if (rowCount > 0)
                {
                    Console.WriteLine("DatabasePublisher - Publishing last {0} remaining rows ...", rowCount);
                    sql.Insert(0, @"INSERT INTO Public_Object_Index VALUES ");
                    SqlCommand command = new SqlCommand(sql.ToString(0, sql.Length - 1), con);
                    command.ExecuteNonQuery();
                }

                Console.WriteLine("DatabasePublisher - Publication successfull ...");
            }
        }

        private const string conString = @"Timeout=10; User Id=Stud;Password=DA3Mon;Integrated Security=false;Initial Catalog=merxbj;Server=tcp:192.168.202.1";
    }
}
