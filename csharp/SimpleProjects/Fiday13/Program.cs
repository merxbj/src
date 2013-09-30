using System;
using System.Collections.Generic;

namespace Fiday13
{
    class Program
    {
        static void Main()
        {
            Dictionary<DayOfWeek, int> days = new Dictionary<DayOfWeek, int>();
            InitDays(days);

            Dictionary<int, int> daysOfMonth = new Dictionary<int, int>();
            InitDaysOfMonth(daysOfMonth);

            DateTime dt = DateTime.MinValue;
            while (dt < DateTime.MaxValue)
            {
                if (dt.Day == 13)
                {
                    int count = days[dt.DayOfWeek];
                    days[dt.DayOfWeek] = count + 1;
                }

                if (dt.DayOfWeek == DayOfWeek.Friday)
                {
                    int count = daysOfMonth[dt.Day];
                    daysOfMonth[dt.Day] = count + 1;
                }

                try
                {
                    dt = dt.AddDays(1);
                }
                catch (ArgumentOutOfRangeException)
                {
                    dt = DateTime.MaxValue;
                }
            }

            ShowDays(days);
            Console.WriteLine();
            ShowDaysOfMonth(daysOfMonth);
            Console.In.ReadLine();
        }

        private static void InitDaysOfMonth(Dictionary<int, int> daysOfMonth)
        {
            for (int i = 1; i <= 31; i++)
            {
                daysOfMonth[i] = 0;
            }
        }

        private static void InitDays(Dictionary<DayOfWeek, int> days)
        {
            days[DayOfWeek.Friday] = 0;
            days[DayOfWeek.Monday] = 0;
            days[DayOfWeek.Saturday] = 0;
            days[DayOfWeek.Sunday] = 0;
            days[DayOfWeek.Thursday] = 0;
            days[DayOfWeek.Tuesday] = 0;
            days[DayOfWeek.Wednesday] = 0;
        }

        private static void ShowDays(Dictionary<DayOfWeek, int> days)
        {
            Console.Out.WriteLine(String.Format("Statistics From {0} to {1}", DateTime.MinValue, DateTime.MaxValue));
            foreach (var entry in days)
            {
                DayOfWeek day = entry.Key;
                int count = entry.Value;

                Console.Out.WriteLine(String.Format("{0} appeared {1} times", day, count));
            }
        }

        private static void ShowDaysOfMonth(Dictionary<int, int> daysOfMonth)
        {
            Console.Out.WriteLine(String.Format("Statistics From {0} to {1}", DateTime.MinValue, DateTime.MaxValue));
            foreach (var entry in daysOfMonth)
            {
                int day = entry.Key;
                int count = entry.Value;

                Console.Out.WriteLine(String.Format("{0} appeared {1} times", day, count));
            }
        }
    }
}
