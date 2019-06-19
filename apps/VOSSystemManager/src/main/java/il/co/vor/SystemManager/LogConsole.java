package il.co.vor.SystemManager;

import il.co.vor.SocketLogRecord;

class LogConsole implements Runnable {
	Console m_console;
	SocketLogRecord m_slr;
    LogConsole(Console _console, SocketLogRecord _slr) { m_console = _console; m_slr = _slr; }
    public void run() {
    	m_console.onLog(m_slr);
    }
}