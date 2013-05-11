using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Integri.Common
{
    public interface IServiceComponent
    {
        void Shutdown();
        void Abort();
        void Initialize();
        void Start();
        void Pause();
        void Resume();
    }
}
