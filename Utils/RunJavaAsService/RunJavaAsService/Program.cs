using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using System.Threading;

namespace WindowsServiceRunJavaProcess
{
    static class Program
    {

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
            string[] _args;
            ServiceBase[] ServicesToRun;
            _args = Environment.GetCommandLineArgs();

            if (_args.Length == 3)
            {
                ServicesToRun = new ServiceBase[]
                {
                new svcRunJavaProcess(_args)
                };
                if (System.Diagnostics.Debugger.IsAttached)
                {
                    ((svcRunJavaProcess)ServicesToRun[0]).runService();
                    Console.ReadKey();
                    ((svcRunJavaProcess)ServicesToRun[0]).stopService();
                }
                else
                {
                    ServiceBase.Run(ServicesToRun);
                }
            }
        }
    }
}
