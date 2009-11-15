using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;

namespace Fiday13
{
    class Program
    {
        static void Main(string[] args)
        {
            Hashtable days = new Hashtable();
            initDays(days);

            DateTime dt = DateTime.MinValue;
            while (dt < DateTime.MaxValue)
            {
                if (dt.Day == 13)
                {
                    int count = (int)days[dt.DayOfWeek];
                    days[dt.DayOfWeek] = count + 1;
                }
                try
                {
                    dt = dt.AddDays(1);
                }
                catch (ArgumentOutOfRangeException ex)
                {
                    dt = DateTime.MaxValue;
                }
            }

            showDays(days);
            System.Console.In.ReadLine();
        }

        private static void initDays(Hashtable days)
        {
            days[DayOfWeek.Friday] = (int)0;
            days[DayOfWeek.Monday] = (int)0;
            days[DayOfWeek.Saturday] = (int)0;
            days[DayOfWeek.Sunday] = (int)0;
            days[DayOfWeek.Thursday] = (int)0;
            days[DayOfWeek.Tuesday] = (int)0;
            days[DayOfWeek.Wednesday] = (int)0;
        }

        private static void showDays(Hashtable days)
        {
            System.Console.Out.WriteLine(String.Format("Statistics From {0} to {1}", DateTime.MinValue, DateTime.MaxValue));
            foreach (DictionaryEntry de in days)
            {
                DayOfWeek day = (DayOfWeek)de.Key;
                int count = (int)de.Value;

                System.Console.Out.WriteLine(String.Format("{0} appeared {1} times", day, count));
            }
        }
    }
}
