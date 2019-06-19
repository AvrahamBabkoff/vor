package il.co.vor.Modbus;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;
import il.co.vor.DalDataClient.DalDataClient;
import il.co.vor.DalDataObjects.CalcOperandProperty;
import il.co.vor.common.Constants;
import il.co.vor.common.VosDataResources;

public class CalcOperandsCompiler {
	
	private static Logger _logger = Logger.getLogger(CalcOperandsCompiler.class.getName());
	JavaCompiler m_compiler = null;
	
	private static Map<String, CalcOperandProperty> m_properties = null;
	ApiMultiResultWrapper<CalcOperandProperty> amrwrproperties = null;
	
	private static ClassLoader m_loader = null;
	private static DiagnosticCollector<JavaFileObject> m_diagnostics = null;
	private static StandardJavaFileManager m_fileManager = null;
	private static StringBuilder  m_source = null;
	
	private static String m_class_name = Constants.NPL_COMPILED_CLASS;
	
	private static String m_source_pref = "import java.util.HashMap;\n"+
			"import java.util.Map;\n"+
			"import java.util.logging.Level;\n"+
			"import java.util.logging.Logger;\n"+
			"import java.util.ArrayList;\n"+
			"import java.time.ZoneId;\n"+
			"import java.time.ZonedDateTime;\n"+
			
			"import il.co.vor.ApiObjectsCommon.ApiMultiResultWrapper;\n"+
			"import il.co.vor.DalDataClient.DalDataClient;\n"+
			"import il.co.vor.common.VosDataResources;\n"+

			"import java.text.DecimalFormat;\n"+
			"import java.text.NumberFormat;\n"+
			"import il.co.vor.DalDataObjects.CalcOperandProperty;\n"+
			"import il.co.vor.Modbus.ICalcOperand;\n"+

			"import il.co.vor.Modbus.OperandWrapper;\n"+

			"public final class " + Constants.NPL_COMPILED_CLASS + " {\n"+
	
				"private static Map<Integer,OperandWrapper> m_operands_values = null;\n"+
				"private static Map<String, Integer> m_operands_id_name = null;\n"+
				"private static Map<String, CalcOperandProperty> m_properties  = null;\n"+
				"private static ApiMultiResultWrapper<CalcOperandProperty> amrwrproperties = null;\n"+
				
				"private static Logger _logger = Logger.getLogger(CalculatedOperands.class.getName());\n"+
				
				"public static NumberFormat formatter = new DecimalFormat(\"#.##\");\n"+
	
				"public static void SetOperandsNames(HashMap<String, Integer> _operands_id_name) {\n"+
					"m_operands_id_name = _operands_id_name;\n"+
				"}\n"+

				"public static void SetOperandsVals(HashMap<Integer, OperandWrapper> _curr_operand_values) {\n"+
					"m_operands_values = _curr_operand_values;\n"+
				"}\n"+
				
				"public static void SetProperties(HashMap <String, CalcOperandProperty> properties) {\n"+
					"m_properties = properties;\n"+
				"}\n"+
	
				"public static String GetPropertyVal(String name) {\n"+
					"String ret = \"\";\n"+
					"CalcOperandProperty cop = m_properties.get(name);\n"+
					"if (cop == null) {\r\n" + 
					"	_logger.log(Level.SEVERE, String.format(\"Cannot find property: %s\", name));\r\n" + 
					"}\n"+
					"ret = m_properties.get(name).getPropertyVal();\n"+
					"return ret;\n"+
				"}\n"+ 
				
				"public static String GetOperandBasicVal(String name) {\n"+
					"String ret = \"\";\n"+
					"int i = m_operands_id_name.get(name);\n"+
					"OperandWrapper op = m_operands_values.get(i);\n"+
					"if (op == null) {\n"+
					"	_logger.log(Level.SEVERE, String.format(\"Cannot read value for operand: %s\", name));\n"+
	                "}\n"+
					"ret = op.GetLogValAsStr();\n"+
					"return ret;\n"+
				"}\n";
	
	private static final File outputFile;

    static {
        String outputPath = System.getProperty("user.dir") +
        		Constants.FILE_SEPARATOR + Constants.NPL_COMPILED_FOLDER;
        outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            try {
                Files.createDirectory(outputFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
        	final File[] files = outputFile.listFiles();
        	for (File f: files) f.delete();
        }
    }
        
	
	public static Iterable<? extends JavaFileObject> getCompilationUnits() {
		m_source.append("}\n");
		
        JavaStringObject stringObject =
                                   new JavaStringObject(Constants.NPL_COMPILED_CLASS, m_source.toString());
        return Arrays.asList(stringObject);
    }
	
	
	public Class GetCompiledClass()
	{
		Class _class = null;
		
		try {
			
			JavaCompiler.CompilationTask task = m_compiler.getTask(null,
			        m_fileManager, m_diagnostics, null, null, getCompilationUnits());
	
			if (!task.call()) {
			    List<Diagnostic<? extends JavaFileObject>> ld = m_diagnostics.getDiagnostics();
			    for(Diagnostic<? extends JavaFileObject> d : ld){
			    	System.out.println(d.getMessage(null));
			    	_logger.log(Level.SEVERE, String.format("Failed to compile CalcOperands.%s",
				    		d.getMessage(null)));
				}		    
			}
			m_fileManager.close();
			
			//try (final URLClassLoader loader = new URLClassLoader(new URL[]{outputFile.toURI().toURL()})) {
			m_loader = new URLClassLoader(new URL[]{outputFile.toURI().toURL()});
			//loading and using our compiled class
				_class = m_loader.loadClass(m_class_name);
			//}
			_class.getMethod("SetProperties",new Class[] { HashMap.class }).invoke(null, new Object[] { m_properties });
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		}
		return _class;
	}
	
	public void Finish()
	{
		
	}
	
	public void AddOperandFormulaData(int oper_id, int calc_oper_id, String name, String prefix, String formula, String update)
	{
		m_source.append("public static class ");
		m_source.append(name);
		m_source.append(" implements ICalcOperand {\n");

		m_source.append("private static int m_operand_id =");
		m_source.append(String.valueOf(oper_id));
		m_source.append(";\n");
		
		m_source.append("private static int m_calc_operand_id =");
		m_source.append(String.valueOf(calc_oper_id));
		m_source.append(";\n");
		
		m_source.append(prefix);
		
		m_source.append("public int GetOperandID() {\n");
		m_source.append("return m_operand_id;\n");
		m_source.append("}\n");
		
		m_source.append("public int GetCalcOperandID() {\n");
		m_source.append("return m_calc_operand_id;\n");
		m_source.append("}\n");
		
		m_source.append("public String GetOperandBasicStrVal(String name) {\n");
		m_source.append("return ");
		m_source.append(Constants.NPL_COMPILED_CLASS);
		m_source.append(".GetOperandBasicVal(name);\n");
		m_source.append("}\n");
	
		m_source.append("public double GetOperandBasicDoubleVal(String name) {\n");
		m_source.append("return  Double.parseDouble(GetOperandBasicVal(name));\n");
		m_source.append("}\n");

		m_source.append("public String Format(double val)\n");
		m_source.append("{\n");
		m_source.append("return ");
		m_source.append(Constants.NPL_COMPILED_CLASS);
		m_source.append(".formatter.format(val);\n");
		m_source.append("}\n");
	    					
//		m_source.append("public void setM_operand_id(int m_operand_id) {\n");
//		m_source.append("this.m_operand_id = m_operand_id;\n");
//	    						
//		m_source.append("}\n");

		m_source.append("public String Calculate(long ulReadCount, ZonedDateTime dtReadTime) {\n");
		
		m_source.append(formula);
		
		m_source.append("}\n");
		
		m_source.append("public String Update(double dValue, ZonedDateTime dt, double dPrevValue) {\n");
		m_source.append(update != null ? update : "return null;\n");
		m_source.append("}\n");
	    					
		m_source.append("}\n");
		
	}
	
	public boolean LoadProperties(int service_id)
	{
		boolean ret = false;
		ArrayList<CalcOperandProperty> _properties = null;
		CalcOperandProperty _property = null;
		
		try {
			m_properties.clear();
			DalDataClient dcc = DalDataClient.getInstance();
			amrwrproperties = dcc.getCalcOperandsProperties().getCalcOperandsPropertiesObject(service_id);
			if (amrwrproperties != null) {
				_properties = amrwrproperties.getApiData().get(VosDataResources.CALC_OPERANDS_PROPERTIES_NAME);
				if ((_properties != null) && (_properties.size() > 0)) {
					for (int i = 0; i < _properties.size(); i++) {
						_property = _properties.get(i);
						m_properties.put(_property.getPropertyName(), _property);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		}
		return ret;
	}
	
	public CalcOperandsCompiler(int service_id)
	{
		try {
			
			m_compiler = ToolProvider.getSystemJavaCompiler();
			m_diagnostics = new DiagnosticCollector<>();
		
			m_fileManager = m_compiler.getStandardFileManager(m_diagnostics, null, null);
		
			m_source = new StringBuilder(m_source_pref);
		
			
			m_fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
			        Arrays.asList(outputFile));
			
			//loader = new URLClassLoader(new URL[]{file.toURI().toURL()});

			m_properties = new HashMap<String, CalcOperandProperty>();
			
			LoadProperties(service_id);
					
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			_logger.log(Level.SEVERE, String.format("Exception: %s", e.getMessage()));
		}
	}

}
