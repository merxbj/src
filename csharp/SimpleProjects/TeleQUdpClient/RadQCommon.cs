using System;
using System.Collections.Generic;
using System.Text;

namespace TeleQUdpClient
{
    [System.Runtime.InteropServices.StructLayoutAttribute(System.Runtime.InteropServices.LayoutKind.Sequential, CharSet = System.Runtime.InteropServices.CharSet.Ansi)]
    public struct RQLOG_EVENT
    {
        /// _RQLOG_EVENT*
        public System.IntPtr Next;

        /// WORD->unsigned short
        public ushort ClientNo;

        /// WORD->unsigned short
        public ushort Group;

        /// SYSTEMTIME->_SYSTEMTIME
        public SYSTEMTIME Time;

        /// DWORD->unsigned int
        public uint ProcessId;

        /// DWORD->unsigned int
        public uint ThreadId;

        /// char[32]
        [System.Runtime.InteropServices.MarshalAsAttribute(System.Runtime.InteropServices.UnmanagedType.ByValTStr, SizeConst = 32)]
        public string Process;

        /// char[32]
        [System.Runtime.InteropServices.MarshalAsAttribute(System.Runtime.InteropServices.UnmanagedType.ByValTStr, SizeConst = 32)]
        public string File;

        /// int
        public int Line;

        /// char[128]
        [System.Runtime.InteropServices.MarshalAsAttribute(System.Runtime.InteropServices.UnmanagedType.ByValTStr, SizeConst = 128)]
        public string Message;

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder(256);
            sb.AppendFormat("{0}\t", ClientNo);
            sb.AppendFormat("{0}\t", Group);
            sb.AppendFormat("{0}\t", ProcessId);
            sb.AppendFormat("{0}\t", ThreadId);
            sb.AppendFormat("{0}\t", Process);
            sb.AppendFormat("{0}\t", File);
            sb.AppendFormat("{0}\t", Line);
            sb.AppendFormat("{0}\t", Time.ToDateTime().ToString());
            sb.AppendFormat("{0}\t", Message);
            return sb.ToString();
        }
    }

    [System.Runtime.InteropServices.StructLayoutAttribute(System.Runtime.InteropServices.LayoutKind.Sequential)]
    public struct SYSTEMTIME
    {
        /// WORD->unsigned short
        public ushort Year;

        /// WORD->unsigned short
        public ushort Month;

        /// WORD->unsigned short
        public ushort DayOfWeek;

        /// WORD->unsigned short
        public ushort Day;

        /// WORD->unsigned short
        public ushort Hour;

        /// WORD->unsigned short
        public ushort Minute;

        /// WORD->unsigned short
        public ushort Second;

        /// WORD->unsigned short
        public ushort Milliseconds;

        /// <summary>
        /// Convert the value to the <see cref="DateTime"/> .NET type.
        /// </summary>
        /// <returns>A valid <see cref="DateTime"/> value or <see cref="DateTime.MinValue"/> if the value stored cannot be represented by <see cref="DateTime"/>.</returns>
        public DateTime ToDateTime()
        {
            try
            {
                return new DateTime(Year, Month, Day, Hour, Minute, Second, Milliseconds);
            }
            catch
            {
                return DateTime.MinValue;
            }
        }
    }
}
