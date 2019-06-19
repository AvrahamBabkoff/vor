package il.co.vor.Modbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalConfigClient.DalConfigClient;
import il.co.vor.DalConfigObjects.CalcOperand;
import il.co.vor.DalConfigObjects.Meter;
import il.co.vor.DalConfigObjects.ModbusChunk;
import il.co.vor.DalConfigObjects.Operand;
import il.co.vor.DalConfigObjects.Plc;
import il.co.vor.DalConfigObjects.Register;
import il.co.vor.Modbus.CalculatedOperands1.*;
import il.co.vor.VOSNetServer.NetServer;
import il.co.vor.common.Constants;
import il.co.vor.common.Enums.FileType;
import il.co.vor.common.ParamNames;
import il.co.vor.common.VosConfigResources;
import il.co.vor.utilities.ParametersReader;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;


public class OperandManager {

	private boolean m_debug = true;
	private static Logger _logger = Logger.getLogger(OperandManager.class.getName());
//	private static ScheduledThreadPoolExecutor m_sch = (ScheduledThreadPoolExecutor) Executors
//			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2); 
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static ScheduledThreadPoolExecutor m_sch = (ScheduledThreadPoolExecutor) Executors
	.newScheduledThreadPool(Constants.NPL_THREADPOOL_THREADS_NUMBER); 
																						// a
																						// thread
																						// pool
																						// for
																						// the
																						// refresh
																						// values
																						// and
																						// write
																						// to
																						// logs
																						// threads
	//private static final ReadWriteLock m_rwl = new ReentrantReadWriteLock(); // read
																				// write
																				// lock
																				// in
																				// order
																				// to
																				// switch
																				// maps
																				// safely

	private static int m_service_id = -1;
	private static Plc m_plc = null;

	

	private static boolean m_in_manual_mode = false; // true if the service is
														// in manual
	// mode, false if auto.
	private static boolean m_in_refresh_mode = false; // true if a new reading
														// has started.

	// Two maps, only one is active. A new reading will be made on the second
	// one, and when done they will be swapped (The second will become the
	// active).
	// The operand_def inside Operand will point to the same object in both
	// maps.
	private static Map<Integer, OperandWrapper> m_operand_values1 = null;
	private static Map<Integer, OperandWrapper> m_operand_values2 = null;
	private static Map<Integer, OperandWrapper> m_curr_operand_values = null; // will
																				// point
																				// to
																				// the
																				// current
																				// map
	
	private static Map<String, Integer> m_operands_id_name = null; // will map operand name to its id. key is operand name.

	//private static List<ChunkWrapper> m_chunks = null; // All chunks data
																// from the DB
																// and
	
	private static List<ModbusChunk> m_chunks = null; // All chunks data from DB

	private static Map<Integer, RegisterWrapper> m_register_operands = null; // All registers data from DB and PLC. key is operand ID.
	
	private static List<Meter> m_meters = null; // All meters data
	
	private static Map<Integer, CalcOperandWrapper> m_calc_operands = null; // All calculated
																			// operands data. key is operand ID.
	
	private static Class m_calc_operands_class = null; // Calculated operands compiles class
	
	private static long m_cycle_number = 0;
	
	private static TreeMap<Integer, CalcOperandWrapper> m_ordered_calc_operands = null; // All calculated operands data ordinal ordered. key is the ordinal.
	
	private static CalcOperandsCompiler m_compiler = null; // Calculated operands compiler

	private static FileObj meters_data_log_file = null; // hold all meters data
														// to be written to
														// meters log file
	private static FileObj operands_data_log_file = null; // hold all data to be
															// written to data
															// log file
	private static FileObj cdrs_data_log_file = null; // hold all cdrs data to
														// be written to cdrs
														// log file

	private static OperandManager m_instance = null; // only one instance of
														// operand manager will
														// be made

	private static ChunkData m_chunk_data = null;
	
	private static OperandsSnapshot m_operands_snapshot = null;
	
	// private static HashMap<String, String> params = null; // all service
	// parameters from DB
	
	private ZonedDateTime m_next_rounded_time = null;
	
	private boolean m_last_log_meters_failed = false;

	public static int getServiceID() {
		return m_service_id;
	}

	public static void setServiceID(int _service_id) {
		m_service_id = _service_id;
	}

	public static Plc getPlc() {
		return m_plc;
	}

	public static void setPlc(Plc _plc) {
		m_plc = _plc;
	}

	public static boolean isInManualMode() {
		return m_in_manual_mode;
	}

	public static void setInManualMode(boolean _in_manual_mode) {
		m_in_manual_mode = _in_manual_mode;
	}

	public static boolean isInRefreshMode() {
		return m_in_refresh_mode;
	}

	public static void setInRefreshMode(boolean m_in_refresh_mode) {
		OperandManager.m_in_refresh_mode = m_in_refresh_mode;
	}

	// create new operand manager, only one instance will be created, will be
	// called only from init
	private OperandManager(int _serviceID, Plc _plc) {

		String prm = "";
		int DataLogInterval = Constants.NPL_DEFAULT_FILE_MAX_INTERVAL;

		setServiceID(_serviceID);
		setPlc(_plc);

		// UpdateParameters(_serviceID);

		prm = ParametersReader.getParameter(ParamNames.NPL_DATA_LOG_INTERVAL, true);
		if (!prm.isEmpty()) {
			DataLogInterval = Integer.parseInt(prm);
		}
		prm = ParametersReader.getParameter(ParamNames.NPL_CDR_DATA_EXPORT_FILE_MAX_INTERVAL, true);
		cdrs_data_log_file = new FileObj(FileType.CDR_DATA, Constants.CDR_DATA_EXPORT_FILE_EXTENSION, getServiceID(),
				prm, DataLogInterval);

		prm = ParametersReader.getParameter(ParamNames.NPL_OPERANDS_DATA_EXPORT_FILE_MAX_INTERVAL, true);
		operands_data_log_file = new FileObj(FileType.OPERANDS_DATA, Constants.OPERANDS_DATA_EXPORT_FILE_EXTENSION,
				getServiceID(), prm, DataLogInterval);

		prm = ParametersReader.getParameter(ParamNames.NPL_METERS_DATA_EXPORT_FILE_MAX_INTERVAL, true);
		meters_data_log_file = new FileObj(FileType.METERS_DATA, Constants.METERS_DATA_EXPORT_FILE_EXTENSION,
				getServiceID(), prm, DataLogInterval);

		_logger.log(Level.INFO, String.format("Create new Operand manager. ServiceID: %s PLC: %s",
				String.valueOf(getServiceID()), getPlc().getPlcName()));
	}

	// return operand manager instance only if exist
	public static OperandManager getInstance() {
		if (getInstance() != null) {
			return getInstance();
		} else {
			throw new AssertionError("Cannot get operand manager instance without init. You have to call init first");
		}

	}

	// return the temporary map (not the current active one)
	private Map<Integer, OperandWrapper> GetTempMap() {
		if (getCurrOperandValues() == getOperandValues1())
			return getOperandValues2();
		else
			return getOperandValues1();
	}

	static Runnable RefreshDBTask = new Runnable() {
		public void run() {
			try {
				_logger.log(Level.INFO, String.format("RefreshDBTask start."));
				ZonedDateTime refresh_time = ZonedDateTime.now(ZoneOffset.UTC);
				m_instance.RefreshDB(refresh_time);
				_logger.log(Level.INFO, String.format("RefreshDBTask end."));
			} catch (Exception e) {
				_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
				e.printStackTrace();
			}
		}
	};

	static Runnable DataLogTask = new Runnable() {
		public void run() {
			try {
				_logger.log(Level.INFO, String.format("DataLogTask start."));
				
				ZonedDateTime refresh_time = ZonedDateTime.now(ZoneOffset.UTC);
				m_instance.DataLog(refresh_time);

				_logger.log(Level.INFO, String.format("DataLogTask end."));
			} catch (Exception e) {
				_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
				e.printStackTrace();

			}
		}
	};

	// init this operand manager. return the operand manager. if not exist,
	// create it.
	public synchronized static OperandManager init(int serviceID, Plc _plc, int iPort) {
		try {

			boolean ok = false;
			String prm = "";

			// intervals
			int RefreshDataInterval = Constants.NPL_DEFAULT_REFRESH_DATA_INTERVAL;
			int DataLogInterval = Constants.NPL_DEFAULT_FILE_MAX_INTERVAL;
			int CDRExportFileMaxInterval = Constants.NPL_DEFAULT_FILE_MAX_INTERVAL;
			int OperandsDataExportFileMaxInterval = Constants.NPL_DEFAULT_FILE_MAX_INTERVAL;
			int METERsDataExportFileMaxInterval = Constants.NPL_DEFAULT_FILE_MAX_INTERVAL;
			GracefullShutdown gsd = new GracefullShutdown();

			NetServer.InitNetServer(Constants.NET_VOS_NPL_BASE_URI, iPort, Constants.NET_VOS_NPL_API_ROOT_PACKAGE_NAME, gsd);
			
			OperandManager op = null;
			int min = -1;

			if (m_instance == null) { // instance not exist, create instance.
				op = new OperandManager(serviceID, _plc);
				ok = (op != null);

				if (ok) {
					ok = op.LoadDB(); // load all data structures
				}

				if (ok) {

					setInstance(op); // set the new instance

					// activate refresh values
					prm = ParametersReader.getParameter(ParamNames.NPL_REFRESH_DATA_INTERVAL, true);
					if (!prm.isEmpty()) {
						RefreshDataInterval = Integer.parseInt(prm);
					}
					m_sch.scheduleAtFixedRate(RefreshDBTask, 0, RefreshDataInterval, TimeUnit.SECONDS); // create
																										// scheduled
																										// thread
																										// every
																										// RefreshDataInterval
																										// for
																										// refreshing
																										// operand
																										// values
					_logger.log(Level.INFO, String.format("Refresh Activated. RefreshDataInterval: %s seconds",
							String.valueOf(RefreshDataInterval)));

				} else // could not build operands data structures
				{
					ok = false;
					clearInstance();
					_logger.log(Level.SEVERE, String.format("Cannot create operands data structures"));
				}

				if (ok) {
					ok = false;

					while (!ok) // forever try to log data
					{
						try {
							// activate data logs
							CDRExportFileMaxInterval = NPLHelper
									.GetIntParam(
											ParametersReader.getParameter(
													ParamNames.NPL_CDR_DATA_EXPORT_FILE_MAX_INTERVAL, true),
											Constants.NPL_DEFAULT_FILE_MAX_INTERVAL);
							OperandsDataExportFileMaxInterval = NPLHelper
									.GetIntParam(
											ParametersReader.getParameter(
													ParamNames.NPL_OPERANDS_DATA_EXPORT_FILE_MAX_INTERVAL, true),
											Constants.NPL_DEFAULT_FILE_MAX_INTERVAL);
							METERsDataExportFileMaxInterval = NPLHelper
									.GetIntParam(
											ParametersReader.getParameter(
													ParamNames.NPL_METERS_DATA_EXPORT_FILE_MAX_INTERVAL, true),
											Constants.NPL_DEFAULT_FILE_MAX_INTERVAL);

							prm = ParametersReader.getParameter(ParamNames.NPL_DATA_LOG_INTERVAL, true);
							if (!prm.isEmpty())
								DataLogInterval = Integer.parseInt(prm);

							// find the minimum interval from the data log
							// intervals , for running the data log scheduled
							// thread
							min = NPLHelper.GetMinVal(CDRExportFileMaxInterval, OperandsDataExportFileMaxInterval,
									METERsDataExportFileMaxInterval, DataLogInterval);

							m_sch.scheduleAtFixedRate(DataLogTask, 0, min, TimeUnit.SECONDS); // create
																								// scheduled
																								// thread
																								// every
																								// minimum
																								// interval
																								// for
																								// data
																								// log
																								// files
							ok = true;

							_logger.log(Level.INFO, String.format(
									"Data Files Log Activated. DataLogInterval(min): %s seconds", String.valueOf(min)));
						} catch (Exception e) {
							ok = false;

							_logger.log(Level.SEVERE,
									String.format("Exception: %s", e.getMessage()));
							e.printStackTrace();

						}
					}

				} else // could not read values, no need to log data. abort.
				{
					clearInstance();
					_logger.log(Level.SEVERE, String.format("Cannot create operands data structures"));
				}
			}
			
			if (OS.indexOf("win") < 0) // linux
			{
				Runtime.getRuntime().addShutdownHook(new Thread()
			     {
			         @Override
			         public void run()
			         {
			        	
			     			_logger.log(Level.WARNING, "GracefullShutdown start.");
			     			// Cancel scheduled but not started task, and avoid new ones
			     			m_sch.shutdown();

			     			// Wait for the running tasks
			     			try {

			     				boolean ret = m_sch.awaitTermination(Constants.THREAD_TERMINATION_TIME, TimeUnit.SECONDS);
			     				_logger.log(Level.WARNING,
			     						String.format("GracefullShutdown after awaitTermination. %s", (ret ? "executor terminated" : "timeout elapsed before termination")));
			     			} catch (InterruptedException e) {
			     				_logger.log(Level.SEVERE,
			     						String.format("Scheduled thread aborted before termination. Exception: %s", e.getMessage()));
			     				e.printStackTrace();
			     			}

			     			// Interrupt the threads and shutdown the scheduler
			     			m_sch.shutdownNow();
			     			
			     			m_instance.Shutdown();
			     			_logger.log(Level.WARNING, "GracefullShutdown end.");
			     
			         }
			     });
			}

		} catch (Exception e) {
			clearInstance();
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();

		}
		return m_instance;

	}

	// build all the data structures and load operand definitions
	public boolean LoadDB() {

		long lStart = 0;
		long lEnd = 0;

		DalConfigClient dcc = DalConfigClient.getInstance();
		
		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "LoadDB Start");

		int RefreshDataInterval = Constants.NPL_DEFAULT_REFRESH_DATA_INTERVAL;
		String prm = "";

		boolean ret = true;
		ApiMultiResultWrapper<ModbusChunk> amrwrchunks = null;
		ApiMultiResultWrapper<Operand> amrwroperands = null;
		ArrayList<Operand> _operands = null;
		ArrayList<ModbusChunk> _chunks = null;

		Operand _oper = null;
		OperandWrapper _operw = null;
		Register _register = null;
		CalcOperand _calcoperand = null;
		RegisterWrapper _registerw = null;
		CalcOperandWrapper _calcoperandw = null;
		
		OperandDef operand_def = null;
		OperandLogCounter operand_log_counter = null;
		
		//Class CalculatedOperands = null;
		m_compiler = new CalcOperandsCompiler(m_plc.getServiceId());
		ICalcOperand _calc_oper_instance = null;
		prm = ParametersReader.getParameter(ParamNames.NPL_REFRESH_DATA_INTERVAL, true);
		if (!prm.isEmpty()) {
			RefreshDataInterval = Integer.parseInt(prm);
		}

		try {

			amrwrchunks = dcc.getPlcs().getModbusChunksObject(getPlc().getPlcId());
			if (amrwrchunks != null) { // chunks exist
				_chunks = amrwrchunks.getApiData().get(VosConfigResources.MODBUS_CHUNKS_NAME);
				if ((_chunks != null) && (!_chunks.isEmpty())) {

					//chunks = new ArrayList<ChunkWrapper>();
					/*for (int i = 0; i < _chunks.size(); i++) {
						chunk = _chunks.get(i);
						chunkw = new Ch
						unkWrapper(chunk);
						chunks.add(chunkw);
						_logger.log(Level.INFO,
								String.format("Chunk added. address: %s type: %s size: %s",
										String.valueOf(chunkw.GetChunkStartAddress()),
										String.valueOf(chunkw.GetChunkType()), String.valueOf(chunkw.GetChunkSize())));
					}*/

					setChunks(_chunks);
				}
			}

			// Load operands
			amrwroperands = dcc.getOperands().getAllOperandsObject(getServiceID());
			if (amrwroperands != null) {
				_operands = amrwroperands.getApiData().get(VosConfigResources.OPERANDS_NAME);
				if ((_operands != null) && (_operands.size() > 0)) {
					// create operands data structures

					m_operand_values1 = new HashMap<Integer, OperandWrapper>();
					m_operand_values2 = new HashMap<Integer, OperandWrapper>();

					setCurrOperandValues(m_operand_values1);

					m_register_operands = new HashMap<Integer, RegisterWrapper>();
					m_calc_operands = new HashMap<Integer, CalcOperandWrapper>();
					m_ordered_calc_operands = new TreeMap<Integer, CalcOperandWrapper>();
					m_meters = new ArrayList<Meter>();
					
					m_operands_id_name = new HashMap<String, Integer>();
					
					// read operand data to operands
					// if register, add also to registers
					// else add to calcs

					for (int i = 0; i < _operands.size(); i++) {
						_oper = _operands.get(i);

						operand_def = new OperandDef(_oper);
						
						if (operand_def.IsLogByInterval())
						{
							operand_log_counter = new OperandLogCounter(RefreshDataInterval,_oper.getOperandLogByIntervalValue());
						}
						
						//operand type can be only register or calc operand
						switch (operand_def.GetOperandType()) {
						case REGISTER:
							_register = _oper.getRegister();
							_registerw = new RegisterWrapper(_oper.getOperandId(), _register.getRegisterId(),
									_register);
							/*if (chunks != null) {
								// find register chunk (if exist)
								for (int j = 0; j < chunks.size(); j++) {
									mchunk = chunks.get(j);
									if (NPLHelper.RegisterInChunk(_registerw, mchunk)) {
										mchunk.SetInUse(true);
										_registerw.SetChunk(mchunk);
										break;
									}
								}
							}*/

							m_register_operands.put(_oper.getOperandId(), _registerw);
							//operand_def.SetRegisterOperand(_register);
							_logger.log(Level.INFO,
									String.format(
											"Operand register added. operand id: %s register id: %s type: %s name: %s size: %s",
											String.valueOf(_registerw.GetOperandID()),
											String.valueOf(_registerw.GetRegisterID()),
											String.valueOf(_registerw.GetRegisterType()), _registerw.GetRegisterName(),
											String.valueOf(_registerw.GetRegisterSize())));
							break;
						case CALC_OPERAND:
							
							_calcoperand = _oper.getCalcOperand();
							_calcoperandw = new CalcOperandWrapper(_oper.getOperandId(),_calcoperand.getCalcOperandId(), _calcoperand);
							m_calc_operands.put(_oper.getOperandId(), _calcoperandw);
							m_ordered_calc_operands.put(_calcoperandw.GetOrdinal(), _calcoperandw);
							
							m_compiler.AddOperandFormulaData(_oper.getOperandId(), _calcoperand.getCalcOperandId(), _calcoperand.getCalcOperandName(), 
									_calcoperand.getCalcOperandFormulaPrefix(), _calcoperand.getCalcOperandFormula(), _calcoperand.getCalcOperandUpdate());
							
							_logger.log(Level.INFO,
									String.format("Operand calc added. operand id: %s calc id: %s name: %s",
											String.valueOf(_oper.getOperandId()),
											String.valueOf(_calcoperand.getCalcOperandId()),
											_calcoperand.getCalcOperandName()));
							break;
						default:
							break;
						}
		
						m_operands_snapshot = new OperandsSnapshot(_operands.size());
						
						// operands objects will point to the same operand_def
						_operw = new OperandWrapper(operand_def, operand_log_counter, new OperandVal());
						m_operand_values1.put(_operw.GetOperandID(), _operw);
						_operw = new OperandWrapper(operand_def, operand_log_counter, new OperandVal());
						m_operand_values2.put(_operw.GetOperandID(), _operw);
						
						if (_oper.getMeter() != null)
						{
							m_meters.add(_oper.getMeter());
						}

						m_operands_id_name.put(_operw.GetOperandName(), _operw.GetOperandID());
					}

					m_chunk_data = new ChunkData();
					ret = ret && m_chunk_data.Init(m_chunks,m_register_operands,getPlc());
					
					/*if (chunks != null) {
						// remove chunks not in use
						Iterator<ChunkWrapper> iter = chunks.iterator();
						while (iter.hasNext()) {
							mchunk = iter.next();
							if (!mchunk.GetInUse()) {
								iter.remove();
								_logger.log(Level.INFO, String.format("Chunk removed. address: %s type: %s size: %s",
										String.valueOf(mchunk.GetChunkStartAddress()),
										String.valueOf(mchunk.GetChunkType()), String.valueOf(mchunk.GetChunkSize())));

							}
						}
					}*/

					
					if (m_calc_operands.size() > 0)
					{
						if (m_debug)
						{
							m_calc_operands_class =  CalculatedOperands1.class;
							
						}
						else
						{
							m_calc_operands_class = m_compiler.GetCompiledClass();
						}
						
						if (m_calc_operands_class != null)
						{
							//Method[] mlist = m_calc_operands_class.getMethods();
							//m_calc_operands_class.getMethod("SetOperandsVals",new Class[] { HashMap.class }).invoke(null, new Object[] { m_curr_operand_values });
							m_calc_operands_class.getMethod("SetOperandsNames",new Class[] { HashMap.class }).invoke(null, new Object[] { m_operands_id_name });
							
							Class<ICalcOperand>[] cl = m_calc_operands_class.getClasses();
							
							if (cl != null && cl.length > 0)
							{
								for (Class<ICalcOperand> co : cl){
						
									try {
										
										_calc_oper_instance = co.newInstance();
										
										_calcoperandw = m_calc_operands.get(_calc_oper_instance.GetOperandID());
										_calcoperandw.SetCalcOperandInstance(_calc_oper_instance);
						
									} catch (Exception e) {
										ret = false;
										_logger.log(Level.SEVERE, String.format("Could not create compiled class for operand ID %s Exception: %s", String.valueOf(_calc_oper_instance.GetOperandID()),e.getMessage()));
									}
								}
							}
							else
							{
								ret = false;
								_logger.log(Level.INFO, String.format("Cannot compile CalcOperands class"));
								throw new Exception("Cannot compile CalcOperands class");
							}
						}
						else
						{
							ret = false;
							_logger.log(Level.INFO, String.format("Cannot compile CalcOperands class"));
							throw new Exception("Cannot compile CalcOperands class");
						}
					}

				} else {
					ret = false;
					_logger.log(Level.INFO, String.format("No operands where found"));
					throw new Exception("No operands where found");
				}
			} else {
				ret = false;
				_logger.log(Level.INFO, String.format("No operands where found"));
				throw new Exception("No operands where found");
			}

		} catch (Exception e) {
			ret = false;
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
			e.printStackTrace();

		}
		_logger.log(Level.INFO, "LoadDB End");
		_logger.log(Level.INFO, String.format("LoadDB Duration: %d milliseconds", (lEnd - lStart)));
		return ret;
	}

	private void SwitchMaps(boolean failed_refresh) throws Exception {

		
		int code = (failed_refresh ? -1 : 0);
		String message = (failed_refresh ? Constants.NPL_FAILED_REFRESH_MESSAGE : "");;
		
		m_operands_snapshot.CreateOperandsSnapshotResult(code, message);
		
		if (m_curr_operand_values == m_operand_values1) {
			m_curr_operand_values = m_operand_values2;
			_logger.log(Level.INFO, String.format("SwitchMaps. current map: 2"));

		} else {
			m_curr_operand_values = m_operand_values1;
			_logger.log(Level.INFO, String.format("SwitchMaps. current map: 1"));
		}

	}

	// write data from queues to files in a different thread
	private synchronized void DataLog(ZonedDateTime refresh_time) throws Exception {
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "DataLog Start");

		int WriteFileMethod = 0;

		String ExportTempPath = "";
		String ExportFinalPath = "";
		String RootDirectory = "";

		/*
		 * int CDRExportFileMaxInterval =
		 * Constants.DEFAULT_EXPORT_FILE_MAX_LINES_NUM; int
		 * LOGDataExportFileMaxInterval =
		 * Constants.DEFAULT_EXPORT_FILE_MAX_LINES_NUM; int
		 * METERDataExportFileMaxInterval =
		 * Constants.DEFAULT_EXPORT_FILE_MAX_LINES_NUM;
		 */

		if ((meters_data_log_file != null) && (operands_data_log_file != null) && (cdrs_data_log_file != null)) {

			operands_data_log_file.setMaxLines(NPLHelper.GetIntParam(
					ParametersReader.getParameter(ParamNames.NPL_OPERANDS_DATA_LOG_MAX_LINES_NUM, true),
					Constants.NPL_DEFAULT_EXPORT_FILE_MAX_LINES_NUM));
			meters_data_log_file.setMaxLines(NPLHelper.GetIntParam(
					ParametersReader.getParameter(ParamNames.NPL_METERS_DATA_LOG_MAX_LINES_NUM, true),
					Constants.NPL_DEFAULT_EXPORT_FILE_MAX_LINES_NUM));
			cdrs_data_log_file.setMaxLines(NPLHelper.GetIntParam(
					ParametersReader.getParameter(ParamNames.NPL_CDR_DATA_LOG_MAX_LINES_NUM, true),
					Constants.NPL_DEFAULT_EXPORT_FILE_MAX_LINES_NUM));

			ExportTempPath = NPLHelper.GetStrParam(ParametersReader.getParameter(ParamNames.NPL_EXPORT_TEMP_PATH, true),
					Constants.NPL_DEFAULT_EXPORT_TEMP_PATH);
			ExportFinalPath = NPLHelper.GetStrParam(
					ParametersReader.getParameter(ParamNames.NPL_EXPORT_FINAL_PATH, true),
					Constants.NPL_DEFAULT_EXPORT_FINAL_PATH);
			RootDirectory = NPLHelper.GetStrParam(ParametersReader.getParameter(ParamNames.NPL_ROOT_DIRECTORY, true),
					Constants.NPL_DEFAULT_ROOT_DIRECTORY);
			WriteFileMethod = NPLHelper.GetIntParam(
					ParametersReader.getParameter(ParamNames.NPL_WRITE_FILE_METHOD, true),
					Constants.NPL_DEFAULT_WRITE_FILE_METHOD);
		}

		operands_data_log_file.UpdateItems(WriteFileMethod, refresh_time, ExportTempPath, ExportFinalPath,
				RootDirectory,
				NPLHelper.GetIntParam(
						ParametersReader.getParameter(ParamNames.NPL_OPERANDS_DATA_EXPORT_FILE_MAX_INTERVAL, true),
						Constants.NPL_DEFAULT_FILE_MAX_INTERVAL));
		meters_data_log_file.UpdateItems(WriteFileMethod, refresh_time, ExportTempPath, ExportFinalPath, RootDirectory,
				NPLHelper.GetIntParam(
						ParametersReader.getParameter(ParamNames.NPL_METERS_DATA_EXPORT_FILE_MAX_INTERVAL, true),
						Constants.NPL_DEFAULT_FILE_MAX_INTERVAL));
		cdrs_data_log_file.UpdateItems(WriteFileMethod, refresh_time, ExportTempPath, ExportFinalPath, RootDirectory,
				NPLHelper.GetIntParam(
						ParametersReader.getParameter(ParamNames.NPL_CDR_DATA_EXPORT_FILE_MAX_INTERVAL, true),
						Constants.NPL_DEFAULT_FILE_MAX_INTERVAL));

		_logger.log(Level.INFO, "DataLog End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("DataLog Duration: %d milliseconds", (lEnd - lStart)));

	}

	// read all operands values
	// refresh is trying to read forever, if exception occurs it will try again
	// from the start in the next scheduled time
	private synchronized void RefreshDB(ZonedDateTime refresh_time) throws Exception {

		boolean ret = false;
		// ZonedDateTime start_time = ZonedDateTime.now(ZoneOffset.UTC);
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "RefreshDB Start");

		if (!isInRefreshMode()) {
			/*m_rwl.readLock().lock();
			_logger.log(Level.INFO, "read lock");*/
			try {

				setInRefreshMode(true);
				ret = m_chunk_data.RefreshData();
				ReadOperandsValues(refresh_time, !ret);

				/*m_rwl.readLock().unlock();
				_logger.log(Level.INFO, "read unlock");*/

				/*m_rwl.writeLock().lock();
				_logger.log(Level.INFO, "write lock");*/

				//try {

					SwitchMaps(!ret); // finished to read new values, update the
									// values

				/*} finally {
					// First read lock ， Then release the lock （ This can be
					// successfully completed ， Read lock before releasing write
					// lock ， Write lock is degraded -- in order to prevent
					// scenario: A- Read B-Read A-Write B-Write Deadlock ）
					m_rwl.readLock().lock();
					_logger.log(Level.INFO, "read lock");
					m_rwl.writeLock().unlock();// Release lock ， Read lock still
												// holds
					_logger.log(Level.INFO, "write unlock");
				}*/
			} catch (Exception e) {

				_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
				e.printStackTrace();
			
			}
			/*} finally {

				m_rwl.readLock().unlock();
			}*/
			setInRefreshMode(false);
		}

		_logger.log(Level.INFO, "RefreshDB End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("RefreshDB Duration: %d milliseconds", (lEnd - lStart)));
	}

	private void ReadOperandsValues(ZonedDateTime refresh_time, boolean failed_refresh)
			throws ModbusIOException, ModbusSlaveException, ModbusException {

		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();

		boolean ret = false;
		OperandWrapper _operw = null;
		
		CalcOperandWrapper _calcoperw = null;

	    Iterator<Map.Entry<Integer, CalcOperandWrapper>> iterator = null;
	    
		Map<Integer, OperandWrapper> tmp_operand_values = GetTempMap(); // this map will hold the new values until finished to read them all
		//ZonedDateTime rounded_time = NPLHelper.GetRoundedHourTime(refresh_time,false);
		String refresh_time_str = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT)
				.format(refresh_time.withZoneSameInstant(ZoneOffset.UTC));
		//String rounded_time_str = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT)
		//		.format(rounded_time.withZoneSameInstant(ZoneOffset.UTC));

		if (m_next_rounded_time == null)
		{
			m_next_rounded_time = NPLHelper.GetRoundedHourTime(refresh_time,false); // only on start
		}
		
		_logger.log(Level.INFO,
				String.format("Read registers values Start. %s operands", String.valueOf(m_register_operands.size())));
		
		m_cycle_number++;
		
		//for (int i = 0; i < m_register_operands.size(); i++) {
		for (RegisterWrapper _registerw : m_register_operands.values()) {
			try {
				//_registerw = m_register_operands.get(i);
				
				_operw = tmp_operand_values.get(_registerw.GetOperandID());
				//oper_def = _operw.GetOperandDef();
				_operw.SetNotValid();

				/*if (failed_refresh)
				{
					_operw.SetEmpty();
				}
				else
				{*/
				if (!failed_refresh)
				{
					// update operandVal inside ChunkData
					ret = m_chunk_data.GetVal(_registerw,_operw);
					
					if (ret)
					{
						 // success
						_logger.log(Level.INFO, String.format(
								"Read register value. operand id: %s register id: %s type: %s name: %s size: %s ref: %s val: %s",
								String.valueOf(_registerw.GetOperandID()),
								String.valueOf(_registerw.GetRegisterID()),
								String.valueOf(_registerw.GetRegisterType()), _registerw.GetRegisterName(),
								String.valueOf(_registerw.GetRegisterSize()), _registerw.GetRegisterRef(),
								_operw.GetLogValAsStr()));
					}
					else
					{
						// failed to read value of one operand, abort.
						_logger.log(Level.SEVERE,
								String.format(
										"failed to read value for operand in m_register_operands register id: %s",
										((_registerw != null) ? String.valueOf(_registerw.GetRegisterID()) : "?")));
					}
				}
					/*if (ret) {

						_operw.setIsFailed(false);

						
					} else {*/
				UpdateLogLists(_operw, refresh_time,refresh_time_str);
				
				m_operands_snapshot.UpdateOperandSnapshot(_operw.GetOperandID(), _operw.GetOperandName(), _operw.GetLogValAsStr(), false);				
				
			} catch (Exception e) {
				ret = false;
				_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
				e.printStackTrace();
				_logger.log(Level.SEVERE,
						String.format("failed to read value for operand in m_register_operands register id: %s",
								((_registerw != null) ? String.valueOf(_registerw.GetRegisterID()) : "?")));

			}
			
			
		}
		
		if (m_calc_operands_class != null)
		{
		
			try {
				
				m_calc_operands_class.getMethod("SetOperandsVals",new Class[] { HashMap.class }).invoke(null, new Object[] { tmp_operand_values });
				
				iterator = m_ordered_calc_operands.entrySet().iterator();
				while(iterator.hasNext()) {
			         
					try {
						
						_calcoperw = (CalcOperandWrapper) iterator.next().getValue();
						_operw = tmp_operand_values.get(_calcoperw.GetOperandID());
						//oper_def = _operw.GetOperandDef();
						_operw.SetNotValid();
	
						/*if (failed_refresh)
						{
							_operw.SetEmpty();
						}
						else
						{*/
						if (!failed_refresh)
						{
							
							// update operandVal inside ChunkData
							ret = _calcoperw.GetVal(_operw, m_cycle_number, refresh_time);
							
	//						CalculatedOperands1.SetOperandsVals(tmp_operand_values);
	//						CalculatedOperands1.SetOperandsIDName(m_operands_id_name);
	//						CalculatedOperands1.LoadProperties(10);
	//						Power_5_Norm_Weight to = new Power_5_Norm_Weight();
	//						String str = to.Calculate(m_cycle_number, refresh_time);
							
							if (ret)
							{
								 // success
								_logger.log(Level.INFO, String.format(
										"Read register value. operand id: %s calc operand id: %s val: %s",
										String.valueOf(_calcoperw.GetOperandID()),
										String.valueOf(_calcoperw.GetCalcOperandID()),
										_operw.GetLogValAsStr()));
							}
							else
							{
								// failed to read value of one operand, abort.
								_logger.log(Level.SEVERE,
										String.format(
												"failed to read value for operand in m_calc_operands calc operand id: %s",
												((_calcoperw != null) ? String.valueOf(_calcoperw.GetCalcOperandID()) : "?")));
							}
						}
	
						UpdateLogLists(_operw, refresh_time,refresh_time_str);
						
						m_operands_snapshot.UpdateOperandSnapshot(_operw.GetOperandID(), _operw.GetOperandName(), _operw.GetLogValAsStr(), false);
						
					} catch (Exception e) {
						ret = false;
						_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
						e.printStackTrace();
						_logger.log(Level.SEVERE,
								String.format("failed to read value for operand in m_calc_operands calc operand id: %s",
										((_calcoperw != null) ? String.valueOf(_calcoperw.GetCalcOperandID()) : "?")));
	
					}
					
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e1) {
				_logger.log(Level.SEVERE, String.format("failed to read calculated operands. Exception: %s", e1.getMessage()));
			}
	    
		}
		
		if (!refresh_time.isBefore(m_next_rounded_time)) 
		{ 
			if (failed_refresh)
			{
				m_last_log_meters_failed = true;
			}
			else
			{
				LogMeters(refresh_time, tmp_operand_values);
				m_next_rounded_time = NPLHelper.GetRoundedHourTime(refresh_time,false);
				m_last_log_meters_failed = false;
			}
		}
		
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("ReadRegistersValues. Duration: %d milliseconds", (lEnd - lStart)));
		_logger.log(Level.INFO, String.format("ReadRegistersValues End"));
	}

	private void LogMeters(ZonedDateTime refresh_time,Map<Integer, OperandWrapper> _operand_values) {
		
		MeterObj metero = null;
		int oper_id = -1;
		OperandWrapper operw = null;
		//OperandDef oper_def = null;
		Operand oper = null;
		OperandVal oper_val = null;
		Meter meter = null;
		
		ZonedDateTime rounded_time = (m_last_log_meters_failed ? refresh_time : NPLHelper.GetRoundedHourTime(refresh_time,true));
		String rounded_time_str = DateTimeFormatter.ofPattern(Constants.CSV_DATE_FORMAT)
				.format(rounded_time.withZoneSameInstant(ZoneOffset.UTC));
		
		for (int j = 0; j < m_meters.size(); j++) {
			
			try {
				
				meter = m_meters.get(j);
				oper = meter.getOperand();
				oper_id = oper.getOperandId();
				operw = _operand_values.get(oper_id);
				//oper_def = operw.GetOperandDef();
				//oper_val = operw.GetOperandVal();
				
				metero = new MeterObj(-1, meter.getMeterId(), rounded_time, rounded_time_str,
						operw.GetLogValAsStr(), 0,
						meter.getMeterTempType(), isInManualMode());
				
				if (!meters_data_log_file.getDataList().offer(metero)) {
					_logger.log(Level.SEVERE,
							String.format("Error: Failed To add new object to meter queue. %s", metero.toString()));
				}
				
				_logger.log(Level.INFO, String.format("operand added to meter log. operand id: %s name: %s",
						String.valueOf(oper.getOperandId()), oper.getOperandName()));
				
			} catch (Exception e) {
				_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
				e.printStackTrace();
				_logger.log(Level.SEVERE,
						String.format(
								"failed to read value for operand m_meters_operands_ids.get(%s) operand id: %s",
								String.valueOf(j), String.valueOf(oper_id)));

			}
		}
	}
	
	private void UpdateLogLists(OperandWrapper new_operw /*the next value (the new) */, ZonedDateTime refresh_time, String refresh_time_str) {

		
		DataObj datao = null;
		
		// the current value (the old)
		OperandWrapper old_operw = null;
		
		old_operw = m_curr_operand_values.get(new_operw.GetOperandID());

		//if ((operw.DataLogOperand(curr_operw.GetDoubleVal(),curr_operw.IsEmpty())) || (curr_operw.IsEmpty() && (!curr_operw.IsFailed()))){
		if (new_operw.DataLogOperand(old_operw.GetDoubleVal(),old_operw.IsValid())) 
		{

			datao = new DataObj(-1, new_operw.GetOperandID(), refresh_time, refresh_time_str,
					new_operw.GetLogValAsStr(), isInManualMode());
			if (!operands_data_log_file.getDataList().offer(datao)) {
				_logger.log(Level.SEVERE,
						String.format("Error: Failed To add new object to datalog queue. %s", datao.toString()));
			}
			//oper_def.SetNextLogTime(refresh_time);
			_logger.log(Level.INFO, String.format("operand added to data log. operand id: %s name: %s",
					String.valueOf(new_operw.GetOperandID()), new_operw.GetOperandName()));
		}

		/*oper_meter = oper_def.GetMeterOperand(); // if it is a meter
		if (oper_meter != null) // if it is a meter
		{

			if (curr_oper_val.IsEmpty()) // its the first time or last time failed, need to update
										// next round hour
			{
				oper_def.SetNextMeterLogTime(rounded_time);
			}
			if (oper_def.MeterLogOperand(refresh_time, operw.IsFailed())) {
				metero = new MeterObj(-1, oper_def.GetMeterOperand().getMeterId(), rounded_time, rounded_time_str,
						NPLHelper.GetLogValAsStr(oper_val, OperandDataType.values()[oper_def.GetOperandDataType()]), 0,
						oper_def.GetMeterOperand().getMeterTempType(), isInManualMode());
				if (!meters_data_log_file.getDataList().offer(metero)) {
					_logger.log(Level.SEVERE,
							String.format("Error: Failed To add new object to meter queue. %s", metero.toString()));
				}
				oper_def.SetNextMeterLogTime(rounded_time);
				_logger.log(Level.INFO, String.format("operand added to meter log. operand id: %s name: %s",
						String.valueOf(oper_def.GetOperandID()), oper_def.GetOperandName()));
			}

		}*/


	}

	public static class GracefullShutdown implements NetServer.ShutdownService {

		@Override
		public void doShutdown() {
			_logger.log(Level.WARNING, "GracefullShutdown start.");
			// Cancel scheduled but not started task, and avoid new ones
			m_sch.shutdown();

			// Wait for the running tasks
			try {

				boolean ret = m_sch.awaitTermination(Constants.THREAD_TERMINATION_TIME, TimeUnit.SECONDS);
				_logger.log(Level.WARNING,
						String.format("GracefullShutdown after awaitTermination. %s", (ret ? "executor terminated" : "timeout elapsed before termination")));
			} catch (InterruptedException e) {
				_logger.log(Level.SEVERE,
						String.format("Scheduled thread aborted before termination. Exception: %s", e.getMessage()));
				e.printStackTrace();
			}

			// Interrupt the threads and shutdown the scheduler
			m_sch.shutdownNow();
			
			m_instance.Shutdown();
			_logger.log(Level.WARNING, "GracefullShutdown end.");
		}

	}

	private void Shutdown() {
		_logger.log(Level.WARNING, "OperandManager final Shutdown start.");
		
		// Final shutdown close opened files
		try {
			
			m_chunk_data.Shutdown(); // close PLC connection
			ZonedDateTime time_now = ZonedDateTime.now(ZoneOffset.UTC);
			DataLog(time_now);
			meters_data_log_file.CloseFile(time_now);
			operands_data_log_file.CloseFile(time_now);
			cdrs_data_log_file.CloseFile(time_now);
				
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("OperandManager final Shutdown aborted before termination. Exception: %s",
					e.getMessage()));
			e.printStackTrace();
		}

		_logger.log(Level.WARNING, "OperandManager Shutdown end.");
	}
	
	public static Map<Integer, OperandWrapper> getOperandValues1() {
		return m_operand_values1;
	}

	public static void setOperandValues1(Map<Integer, OperandWrapper> _operand_values1) {
		m_operand_values1 = _operand_values1;
	}

	public static Map<Integer, OperandWrapper> getOperandValues2() {
		return m_operand_values2;
	}

	public static void setOperandValues2(Map<Integer, OperandWrapper> _operand_values2) {
		m_operand_values2 = _operand_values2;
	}

	public static Map<Integer, OperandWrapper> getCurrOperandValues() {
		return m_curr_operand_values;
	}

	public static void setCurrOperandValues(Map<Integer, OperandWrapper> _curr_operand_values) {
		m_curr_operand_values = _curr_operand_values;
	}

	public static List<ModbusChunk> getChunks() {
		return m_chunks;
	}

	public static void setChunks(List<ModbusChunk> _chunks) {
		m_chunks = _chunks;
	}

	public static Map<Integer, RegisterWrapper> getRegisterOperands() {
		return m_register_operands;
	}

	public static void setRegisterOperands(Map<Integer, RegisterWrapper> _register_operands) {
		m_register_operands = _register_operands;
	}

	public static Map<Integer, CalcOperandWrapper> getCalcOperands() {
		return m_calc_operands;
	}

	public static void setCalcOperands(Map<Integer, CalcOperandWrapper> _calc_operands) {
		m_calc_operands = _calc_operands;
	}

	public static void setInstance(OperandManager _instance) {
		m_instance = _instance;
	}

	public static void clearInstance() {
		m_instance = null;
	}
	
	public static String GetOperandsSnapshotResult()
	{
		return m_operands_snapshot.GetOperandsSnapshotResult();
	}
	
	public static Boolean SetOperandValue(int _operand_id, String _operand_value)
	{
		boolean ret = false;
		RegisterWrapper rw = null;
		OperandWrapper ow = null;
		//update PLC
		
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "OperandManager SetOperandValue Start");
		
		try 
		{
			rw = m_register_operands.get(_operand_id);
			ow = m_curr_operand_values.get(_operand_id);
	
			if ((rw != null) && (ow != null) && (m_chunk_data.SetVal(rw, ow, _operand_value)))
			{
				ret = m_operands_snapshot.UpdateOperandSnapshot(ow.GetOperandID(), ow.GetOperandName(), ow.GetLogValAsStr(), true);
			}
		
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("OperandManager failed to SetOperandValue. Operand ID = %s Operand Value = %s .Exception: %s",
					String.valueOf(_operand_id), _operand_value, e.getMessage()));
			e.printStackTrace();
		}
		
		_logger.log(Level.INFO, "OperandManager SetOperandValue End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("OperandManager SetOperandValue Duration: %d milliseconds", (lEnd - lStart)));
		
		return ret;
	}
	
	public static Boolean ReloadCalcOperandsProperties()
	{
		boolean ret = false;
		//update PLC
		
		long lStart = 0;
		long lEnd = 0;

		lStart = System.currentTimeMillis();
		_logger.log(Level.INFO, "OperandManager ReloadCalcOperandsProperties Start");
		
		try 
		{
			ret = m_compiler.LoadProperties(m_plc.getServiceId());
		
		} catch (Exception e) {
			_logger.log(Level.SEVERE, String.format("OperandManager failed to ReloadCalcOperandsProperties.",
					 e.getMessage()));
			e.printStackTrace();
		}
		
		_logger.log(Level.INFO, "OperandManager ReloadCalcOperandsProperties End");
		lEnd = System.currentTimeMillis();
		_logger.log(Level.INFO, String.format("OperandManager ReloadCalcOperandsProperties Duration: %d milliseconds", (lEnd - lStart)));
		
		return ret;
	}

}
