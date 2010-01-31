using System;
using System.Collections.Generic;
using System.Text;
using System.Data.SqlClient;

namespace Rodokmen.Common
{
    public class ServerConnector
    {
        #region Construction
        ServerConnector()
        {
            ;
        }
        #endregion

        #region Properties
        public static SqlConnection Connection
        {
            get
            {
                if (connection == null)
                    CreateConnection();

                return connection;
            }
        }
        #endregion

        #region Private members
        private static SqlConnection connection;
        #endregion

        #region Helper methods
        private static void CreateConnection()
        {
            String sqlConnectionString = BuildUpConnectionString();
            try
            {
                connection = new SqlConnection(sqlConnectionString);
            }
            catch (SqlException ex)
            {
                ;
            }
        }

        private static String BuildUpConnectionString()
        {
            String newConnectionString = "";
            newConnectionString += @"Data Source=ETERNITY;";
            newConnectionString += @"Initial Catalog=merxbj;";
            newConnectionString += @"User ID=sa;";
            newConnectionString += @"Password=prsten;";

            return newConnectionString;
        }
        #endregion
    }
}
