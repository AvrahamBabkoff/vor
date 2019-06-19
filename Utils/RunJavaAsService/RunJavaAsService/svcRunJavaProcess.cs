using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.IO;
using System.Configuration;
using System.Net.Http;
using System.Net;

namespace WindowsServiceRunJavaProcess
{
    public partial class svcRunJavaProcess : ServiceBase
    {
        private const string CURRENT_DIRECTORY_PROPERTY_NAME = "CurrentDirectory";
        private const string SERVICE_CONFIG_FILE_NAME_PROPERTY_NAME = "ServiceConfigFileName";
        private const string SHUTDOWN_URL_FULL_FILE_NAME_FORMAT = "{0}\\{1}_shutdown_url.txt";
        private const string PID_FULL_FILE_NAME_FORMAT = "{0}\\{1}.pid";
        private const string JAVA_COMMAND = "javaw";
        private const string JAVA_ARGUMENTS_FORMAT = "-Dil.co.vor.ShutdownUrlFileName=\"{0}\" -Dil.co.vor.ServiceName=\"{1}\" -cp lib/*;classes/ {2}";
        private const double SHUTDOWN_API_TIMEOUT_IN_MILLISECONDS = 10000;
        private const int SHUTDOWN_GRACE_TIME_IN_MILLISECONDS = 10000;

        private FileStream m_fs;
        private StreamReader m_sr;
        private string m_sShutdownUrlFileName;
        private string m_sPidFileName;
        private readonly Object m_Lock;

        bool m_bProcessExited;
        Process m_Process = null;
        string[] m_args;

        public svcRunJavaProcess(string[] _args)
        {
            m_args = _args;
            InitializeComponent();
            ServicePointManager.ServerCertificateValidationCallback += (sender, cert, chain, sslPolicyErrors) => true;
            m_bProcessExited = false;
            m_Lock = new Object();
        }

        public void runService()
        {
            StartJavaProcess();
        }

        public void stopService()
        {
            OnStop();
        }

        protected override void OnStart(string[] args)
        {
            Thread oThread = new Thread(new ThreadStart(StartJavaProcess));
            oThread.Start();
        }

        private void StartJavaProcess()
        {
            string sLine;
            string[] columnValues;
            string sCurrentDir = ConfigurationManager.AppSettings[CURRENT_DIRECTORY_PROPERTY_NAME];
            string sConfigFileName = ConfigurationManager.AppSettings[SERVICE_CONFIG_FILE_NAME_PROPERTY_NAME];


            bool bFound = false;
            string sServiceName = "";
            string sMainClass = "";

            Directory.SetCurrentDirectory(sCurrentDir);
            m_fs = File.OpenRead(sConfigFileName);
            m_sr = new StreamReader(m_fs);
            // read first line. 
            if (!m_sr.EndOfStream)
            {
                sLine = m_sr.ReadLine();
            }

            while (!m_sr.EndOfStream)
            {
                
                sLine = m_sr.ReadLine();
                columnValues = sLine.Split(',');
                if (columnValues.Length != 2)
                {
                    break;
                }
                if (m_args[1].Equals(columnValues[0]))
                {
                    bFound = true;
                    //sServiceName = columnValues[1];
                    sServiceName = m_args[2];
                    sMainClass = columnValues[1];
                    break;
                }
            }
            if (true == bFound)
            {
                //m_sShutdownUrlFileName = sCurrentDir + "\\" + sServiceName + "_shutdown_url.txt";
                m_sShutdownUrlFileName = string.Format(SHUTDOWN_URL_FULL_FILE_NAME_FORMAT, sCurrentDir, sServiceName);
                m_sPidFileName = string.Format(PID_FULL_FILE_NAME_FORMAT, sCurrentDir, sServiceName);
                m_Process = new Process();
                m_Process.StartInfo.UseShellExecute = false;

                //m_Process.StartInfo.FileName = "javaw";
                m_Process.StartInfo.FileName = JAVA_COMMAND;

                m_Process.EnableRaisingEvents = true;
                m_Process.Exited += new EventHandler(Process_Exited);
                //m_Process.StartInfo.Arguments = "-Dil.co.vor.ShutdownUrlFileName=\"" + m_sShutdownUrlFileName + "\" " + "-Dil.co.vor.ServiceName=\"" + sServiceName + "\" -cp lib/*;classes/ " + sMainClass; // ""DAL_CONFIG""" - cp lib/*;classes/ il.co.vor.DalConfig.DalConfig
                m_Process.StartInfo.Arguments = string.Format(JAVA_ARGUMENTS_FORMAT, m_sShutdownUrlFileName, sServiceName, sMainClass);
                m_Process.StartInfo.CreateNoWindow = true;
                m_Process.Start();
                File.WriteAllText(m_sPidFileName, m_Process.Id.ToString());
            }
            m_sr.Close();
            m_fs.Close();
        }

        private void Process_Exited(object sender, System.EventArgs e)
        {
            lock (m_Lock)
            {
                m_bProcessExited = true;
                Stop();
            }
        }

        private void StopJavaProcess()
        {
            string sShutdownUrl;
            HttpResponseMessage responseMessage;
            string responseString;

            if ((false == m_bProcessExited) && (null != m_Process))
            {
                try
                {
                    sShutdownUrl = File.ReadAllText(m_sShutdownUrlFileName);
                    using (HttpClient client = new HttpClient())
                    {
                        client.Timeout = TimeSpan.FromMilliseconds(SHUTDOWN_API_TIMEOUT_IN_MILLISECONDS);
                        Task<HttpResponseMessage> task = client.PostAsync(sShutdownUrl, null);
                        responseMessage = task.Result;

                        responseString = responseMessage.Content.ReadAsStringAsync().Result;
                        Thread.Sleep(SHUTDOWN_GRACE_TIME_IN_MILLISECONDS);
                    }
                }
                catch (Exception ex)
                {
                    string sException = ex.Message;
                }
                try
                {
                    if (false == m_bProcessExited)
                    {
                        m_Process.Kill();
                        m_Process.Dispose();
                    }
                }
                catch (Exception ex)
                {
                    string sException = ex.Message;
                }
            }
        }
        protected override void OnStop()
        {
            lock (m_Lock)
            {
                StopJavaProcess();
            }
            /*
            string sShutdownUrl;
            HttpResponseMessage responseMessage;
            string responseString;
            m_bStopRequest = true;

            if ((false == m_bProcessExited) && (null != m_Process))
            {
                try
                {
                    sShutdownUrl = File.ReadAllText(m_sShutdownUrlFileName);
                    using (HttpClient client = new HttpClient())
                    {
                        client.Timeout = TimeSpan.FromMilliseconds(SHUTDOWN_API_TIMEOUT_IN_MILLISECONDS);
                        Task<HttpResponseMessage> task = client.PostAsync(sShutdownUrl, null);
                        responseMessage = task.Result;

                        responseString = responseMessage.Content.ReadAsStringAsync().Result;
                        Thread.Sleep(SHUTDOWN_GRACE_TIME_IN_MILLISECONDS);
                    }
                }
                catch (Exception ex)
                {
                    string sException = ex.Message;
                }
                try
                {
                    if (false == m_bProcessExited)
                    {
                        m_Process.Kill();
                        m_Process.Dispose();
                    }
                }
                catch (Exception ex)
                {
                    string sException = ex.Message;
                }            
            }
            */
        }



    }
}
