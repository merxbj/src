using Integri.Common.DataAccess;
using Integri.Common.Publishing;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;

namespace Integri.Indexer.PublicNameIndexing
{
    class DatabasePublisher : DataAccessLayer, IPublisher<Occurrence>
    {
        #region IPublisher Implementation

        public void Publish(List<Occurrence> occurrences)
        {
            Log.InfoFormat("About to publish {0} indexing results ...", occurrences.Count());

            CleanUpIndex();

            PublishIndex(occurrences);

            RecordEvent();

            Log.InfoFormat("Publication successfull ...");
        }

        #endregion

        #region Private Methods

        private void PublishIndex(IEnumerable<Occurrence> occurrences)
        {
            int rowCount = 0;
            StringBuilder sql = new StringBuilder("");
            foreach (Occurrence o in occurrences)
            {
                if (o.PublicObject.Type == ObjectType.Event)
                {
                    if (rowCount == 1000)
                    {
                        Log.InfoFormat("Publishing {0} rows ...", rowCount);
                        sql.Insert(0, @"INSERT INTO Public_Object_Index VALUES ");
                        SqlCommand command = new SqlCommand(sql.ToString(0, sql.Length - 1), Connection);
                        command.ExecuteNonQuery();
                        sql.Clear();
                        rowCount = 0;
                    }

                    EventOccurence e = (EventOccurence)o;
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
                Log.InfoFormat("Publishing last {0} remaining rows ...", rowCount);
                sql.Insert(0, @"INSERT INTO Public_Object_Index VALUES ");
                SqlCommand command = new SqlCommand(sql.ToString(0, sql.Length - 1), Connection);
                command.ExecuteNonQuery();
            }
        }

        private void CleanUpIndex()
        {
            Log.Info("Cleaning up old results ...");

            SqlCommand cleanUp = new SqlCommand("DELETE FROM Public_Object_Index", Connection);
            cleanUp.ExecuteNonQuery();
        }

        private void RecordEvent()
        {
            Log.Info("Recording a new publishing event ...");

            SqlCommand eventRecord = new SqlCommand("INSERT INTO Indexing_EVENT (timestamp) VALUES (GETDATE())", Connection);
            eventRecord.ExecuteNonQuery();
        }

        #endregion
    }
}
