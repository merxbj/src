using System;
using System.Collections.Generic;
using System.ServiceProcess;
using System.Threading;
using Integri.Common.Logging;
using log4net;
using Integri.Common;

namespace Integri.ProjectSupportService
{
    public partial class Service : ServiceBase
    {
        public Service()
        {
            InitializeComponent();
            ServiceName = Constants.ServiceShortName;
            CanStop = true;
            CanPauseAndContinue = true;
            AutoLog = true;
        }

        protected override void OnContinue()
        {
            log.Info("Temporarily disabling the Integri Project Support Service.");
            try
            {
                services.ForEach(manager => manager.Resume());
            }
            catch (Exception ex)
            {
                log.Fatal("Exception while resuming the Integri Project Support Service", ex);
                throw;
            }
            log.Debug("Managers paused.");
        }

        protected override void OnPause()
        {
            MethodSubjectToTimeout body = Pause;
            ExecuteInTimeLimit(body, "pause");
        }

        protected override void OnStart(string[] args)
        {
            WriteToEventLog("The Integri Project Support Service initialization starts.");
            if (initialized)
            {
                OnContinue();
            }
            else
            {
                Initialize();
                initialized = true;
                Start();
            }
            WriteToEventLog("The Integri Project Support Service: synchronous initialization successfully finishes.");
            log.Debug("The Integri Project Support Service up and running");
        }

        protected override void OnStop()
        {
            MethodSubjectToTimeout body = Shutdown;
            ExecuteInTimeLimit(body, "stop");
            initialized = false;
            base.OnStop();
        }

        protected override void OnShutdown()
        {
            MethodSubjectToTimeout body = Shutdown;
            ExecuteInTimeLimit(body, "shutdown");
            initialized = false;
            base.OnShutdown();
        }

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
            CommandLine.ParseCommandLine();

            if (CommandLine.RunAsService)
            {
                RunAsService();
            }
            else
            {
                RunAsCommandLineApp();
            }
        }

        private static void RunAsCommandLineApp()
        {
            log.Info("Starting the Integri Project Support Service directly from console");
            Initialize();
            Start();
            LetTimePassBy();
            Shutdown();
            log.Info("The Integri Project Support Service stopped");
        }

        private static void RunAsService()
        {
            log.Info("Starting the Integri Project Support Service as a service");
            Service service = new Service();
            Run(service);
            log.Info("The Integri Project Support Service as a service stopped");
        }

        private static void LetTimePassBy()
        {
            Console.Error.WriteLine("(Running.  Press Enter to terminate.)");
            Console.ReadLine();
        }

        private static void Shutdown()
        {
            log.Info("Shuting down the Integri Project Support Service");
            try
            {
                services.ForEach(manager => manager.Shutdown());
            }
            catch (Exception ex)
            {
                log.Fatal("Exception while shuting down the Integri Project Support Service", ex);
                throw;
            }
            log.Info("The Integri Project Support Service successfully shut down");
        }

        private static void Initialize()
        {
            log.Info("Initializing the Integri Project Support Service");
            try
            {
                // get service instances first
                services.AddRange(Utils.Discover<IServiceComponent>());

                // initialize them second
                services.ForEach(manager => manager.Initialize());
            }
            catch (Exception ex)
            {
                log.Fatal("Exception while initializing Integri Project Support Service", ex);
                throw;
            }
        }

        private static void Start()
        {
            log.Info("Starting the Integri Project Support Service");
            try
            {
                services.ForEach(manager => manager.Start());
            }
            catch (Exception ex)
            {
                log.Fatal("Exception while starting the Integri Project Support Service", ex);
                throw;
            }
        }

        private static void Pause()
        {
            log.Info("Temporarily disabling the Integri Project Support Service");
            try
            {
                services.ForEach(manager => manager.Pause());
            }
            catch (Exception ex)
            {
                log.Fatal("Exception while pausing the Integri Project Support Service", ex);
                throw;
            }
            log.Debug("The Integri Project Support Service paused.");
        }

        private void WriteToEventLog(string message)
        {
            try
            {
                EventLog.WriteEntry(message);
            }
            catch (Exception e)
            {
                // consume
                log.Debug("Cannot write to system event log: {0}", e);
            }
        }

        #region Pause/Stop/Shutdown timeout support

        public delegate void MethodSubjectToTimeout();

        /// <summary>
        /// Execute a method in a fixed time limit, abort if exceeded.
        /// </summary>
        /// <param name="body">Method to be run.</param>
        /// <param name="label">Verb describing <paramref name="body"/> for the purpose of logging.</param>
        private static void ExecuteInTimeLimit(MethodSubjectToTimeout body, string label)
        {
            TimerCallback timeoutCallback = StopTimeout;
            const long timeout = 28 * 1000;
            using (Timer timer = new Timer(timeoutCallback, Thread.CurrentThread, timeout, Timeout.Infinite))
            {
                try
                {
                    body();
                    timer.Change(Timeout.Infinite, Timeout.Infinite);
                }
                catch (ThreadAbortException)
                {
                    log.Fatal(String.Format("Time limit of {0} ms exceeded while trying to {1} the Integri Project Support Service", timeout, label));
                    Thread.ResetAbort();
                }
                catch (Exception e)
                {
                    log.Fatal("Exception thrown from service callback: {0}", e);
                }
            }
        }

        private static void StopTimeout(object state)
        {
            Thread shutdown = state as Thread;
            if (shutdown != null)
            {
                shutdown.Abort();
            }
        }

        #endregion

        // this probably includes the configuration of logging:
        private static readonly ILog log = LoggingFactory.GetLogger();
        private static bool initialized;

        private static readonly List<IServiceComponent> services = new List<IServiceComponent>(2);
    }
}
