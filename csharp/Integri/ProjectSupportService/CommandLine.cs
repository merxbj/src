using System;
using System.Collections;
using System.Reflection;

namespace Integri.ProjectSupportService
{
    /// <summary>
    /// This "static" class encapsulates command line handling.
    /// Add new command line parameters by simply subclassing <see cref="CommandLineParameter"/>.
    /// </summary>
    internal class CommandLine
    {
        #region Generic instantiation and parsing

        /// <summary>
        /// Requested to run as a service (implicit).
        /// </summary>
        internal static bool RunAsService
        {
            get { return runAsService; }
        }

        internal static bool Debug
        {
            get { return debug; }
        }

        private static bool debug;
        private static bool runAsService = true;

        /// <summary>
        /// Parse command line.  Set any read-only properties that need to.
        /// </summary>
        internal static void ParseCommandLine()
        {
            DefineAvailableParameters();

            string[] commandLineArgs = Environment.GetCommandLineArgs();

            if (commandLineArgs.Length == 1)
            {
                Console.Error.WriteLine("Trying to run a service .exe directly is unusual.");
                Console.Error.WriteLine("Consider specifying /noservice or /help on the command line.");
            }
            for (int i = 1; i < commandLineArgs.Length; i++) // skips commandLineArgs[0]
            {
                if (commandLineArgs[i].Trim().Length > 0)
                {
                    CommandLineParameter.TryAllParameters(commandLineArgs[i]);
                }
            }
        }

        private static void DefineAvailableParameters()
        {
            Type myself = typeof(CommandLine);
            Type baseParameter = myself.GetNestedType(typeof(CommandLineParameter).Name);
            Type[] candidates = myself.GetNestedTypes(BindingFlags.NonPublic | BindingFlags.Public);
            foreach (Type parameter in candidates)
            {
                if (parameter.IsSubclassOf(baseParameter) && !parameter.IsAbstract)
                {
                    Activator.CreateInstance(parameter);
                }
            }
        }

        #endregion

        #region Individual command line options

        /// <summary>
        /// Base class for command line options.  Just subclass it to provide the options.
        /// The subclasses must be nested in <see cref="CommandLine"/>.
        /// </summary>
        public abstract class CommandLineParameter
        {
            protected CommandLineParameter(string keyword, string summary)
            {
                this.keyword = keyword;
                this.summary = summary;
                if (keyword.Length > longestParameterKeyword)
                {
                    longestParameterKeyword = keyword.Length;
                }
                Instances.Enqueue(this);
            }

            /// <summary>
            /// Identify whether a word of the command line sets
            /// this parameter (a command line switch).
            /// </summary>
            /// <param name="word">Command line word.</param>
            /// <returns></returns>
            internal bool Matches(string word)
            {
                return String.Compare(word, "/" + keyword, true) == 0;
            }

            /// <summary>
            /// Write usage summary for this parameter to the standard error output.
            /// </summary>
            internal void Print()
            {
                Console.Error.WriteLine("/" + keyword.PadRight(longestParameterKeyword + 2) + summary);
            }

            /// <summary>
            /// Apply this parameter (a command line switch), because it
            /// should be set.
            /// </summary>
            internal abstract void Apply();

            /// <summary>
            /// Tries to apply all possible parameters on a word in turn.
            /// </summary>
            /// <param name="word">Command line word.</param>
            internal static void TryAllParameters(string word)
            {
                foreach (CommandLineParameter param in Instances)
                {
                    if (param.Matches(word))
                    {
                        param.Apply();
                        return;
                    }
                }
                Console.Error.WriteLine("Unrecognized command line parameter " + word);
                Environment.Exit(1);
            }

            private readonly string keyword;
            private readonly string summary;

            private static int longestParameterKeyword = 5; // increases automatically as needed
            protected static Queue Instances = new Queue();
        }

        private class NoserviceParameter : CommandLineParameter
        {
            public NoserviceParameter()
                : base("noservice", "Run as a console app, not a service")
            {
            }

            protected NoserviceParameter(string keyword, string summary)
                : base(keyword, summary)
            {
            }

            internal override void Apply()
            {
                runAsService = false;
            }
        }

        private class HelpParameter : CommandLineParameter
        {
            public HelpParameter()
                : base("help", "Provide this help and exit")
            {
            }

            protected HelpParameter(string keyword)
                : base(keyword, "(same as /help)")
            {
            }

            internal override void Apply()
            {
                foreach (CommandLineParameter param in CommandLineParameter.Instances)
                {
                    param.Print();
                }
                Environment.Exit(0);
            }
        }

        private class QuestionMarkParameter : HelpParameter
        {
            public QuestionMarkParameter()
                : base("?")
            {
            }
        }

        private class DebugParameter : CommandLineParameter
        {
            public DebugParameter()
                : base("debug", "Mock the order bridge, no import gets processed by RPOS.")
            {
            }

            protected DebugParameter(string keyword, string summary)
                : base(keyword, summary)
            {
            }

            internal override void Apply()
            {
                debug = true;
            }
        }

        #endregion
    }
}