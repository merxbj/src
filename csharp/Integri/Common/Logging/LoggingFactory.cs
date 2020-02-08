using System;
using System.Configuration;
using System.Diagnostics;
using System.Xml;
using log4net;
using log4net.Config;

namespace Integri.Common.Logging
{
    public static class LoggingFactory
    {
        public static ILog GetLogger()
        {
            var declaringType = new StackTrace().GetFrame(1).GetMethod().DeclaringType;
            if (declaringType != null)
            {
                string callerClassName = declaringType.Name;
                return GetLogger(callerClassName);
            }
            return GetLogger(typeof(LoggingFactory).Name);
        }

        private static ILog GetLogger(string name)
        {
            if (!initialized)
            {
                if (!ConfigureLogging())
                {
                    ConfigureBackupLogging();
                }
            }
            return LogManager.GetLogger(name);
        }

        private static void ConfigureBackupLogging()
        {
            BasicConfigurator.Configure();
            initialized = true;
        }

        private static bool ConfigureLogging()
        {
            XmlElement loggingConfigXml = (XmlElement)ConfigurationManager.GetSection("log4net");
            if (loggingConfigXml != null)
            {
                XmlConfigurator.Configure(loggingConfigXml);
                initialized = true;
            }
            else
            {
                Console.WriteLine("Logging configuration not found.");
                initialized = false;
            }
            return initialized;
        }

        private static bool initialized;
    }
}
