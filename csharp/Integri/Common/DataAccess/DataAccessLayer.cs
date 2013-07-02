using System;
using System.Data.SqlClient;
using Integri.Common.Logging;
using log4net;

namespace Integri.Common.DataAccess
{
    public abstract class DataAccessLayer : IDisposable
    {
        #region Construction & Disposal

        protected DataAccessLayer()
        {
            con = new SqlConnection(conString);
            try
            {
                con.Open();
            }
            catch (Exception ex)
            {
                Log.Fatal("Failed to open the database connection!", ex);
                throw;
            }
            
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!disposed)
            {
                if (disposing)
                {
                    con.Dispose();
                }
                disposed = true;
            }
        }

        ~DataAccessLayer()
        {
            // Simply call Dispose(false).
            Dispose(false);
        }

        #endregion

        #region Protected Properties

        protected SqlConnection Connection { get { return con; } }

        #endregion

        #region Private Fields

        private const string conString = @"Timeout=10; User Id=Stud;Password=DA3Mon;Integrated Security=false;Initial Catalog=merxbj;Server=tcp:192.168.202.1";
        private bool disposed;
        private readonly SqlConnection con;
        protected ILog Log = LoggingFactory.GetLogger();

        #endregion
    }
}
