import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;


import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import il.co.vor.Modbus.ICalcOperand;
import il.co.vor.Modbus.JavaStringObject;
import il.co.vor.common.Constants;



public class TestCalculated {
	
	 public static Iterable<? extends JavaFileObject> getCompilationUnits() {
	        JavaStringObject stringObject =
	                                   new JavaStringObject("CalculatedOperands", getSource());
	        return Arrays.asList(stringObject);
	    }

	    public static String getSource() {
	    	String source = 
	    			"import java.util.HashMap;\n"+
	    	        "import il.co.vor.common.Constants;\n"+
	    			"import il.co.vor.Modbus.ICalcOperand;\n"+
	    			"import il.co.vor.Modbus.JavaStringObject;\n"+

	    			"public final class CalculatedOperands {\n"+
	    				
	    				"private static HashMap <Integer,String> m_operands_values = null;\n"+
	    				
	    				"public static void SetOperandsVals() {\n"+
	    					"m_operands_values = new HashMap<Integer,String>();\n"+
	    					"m_operands_values.put(1, \"John\");\n"+
	    					"m_operands_values.put(2, \"Doe\");\n"+
	    				"}\n"+
	    				
	    				"public static String GetOperandBasicVal(int i) {\n"+
	    					"String ret = \"\";\n"+
	    					"ret = m_operands_values.get(i);\n"+
	    					"return ret;\n"+
	    				"}\n"+
	    				
	    				"public static class Operand_1 implements ICalcOperand {\n"+

	    					"private static int m_operand_id = 1;\n"+
	    					"public static int getM_operand_id() {\n"+
	    						"return m_operand_id;\n"+
	    					"}\n"+
	    					
	    					"public void setM_operand_id(int m_operand_id) {\n"+
	    						"this.m_operand_id = m_operand_id;\n"+
	    						
	    					"}\n"+
	    					
	    					"public String Calculate() {\n"+
	    						"int RefreshDataInterval = Constants.NPL_DEFAULT_REFRESH_DATA_INTERVAL;\n"+
	    						"String name = CalculatedOperands.GetOperandBasicVal(getM_operand_id());\n"+
	    						"return \"Hello 1 \"+name;\n"+
	    					"}\n"+
	    					
	    				"}\n"+
	    				
	    				"public static class Operand_2 implements ICalcOperand {\n"+

	    					"private static int m_operand_id = 2;\n"+
	    					"public static int getM_operand_id() {\n"+
	    						"return m_operand_id;\n"+
	    					"}\n"+
	    					
	    					"public void setM_operand_id(int m_operand_id) {\n"+
	    						"this.m_operand_id = m_operand_id;\n"+
	    						
	    					"}\n"+
	    					
	    					"public String Calculate() {\n"+
	    					    "int RefreshDataInterval = Constants.NPL_DEFAULT_REFRESH_DATA_INTERVAL;\n"+
	    						"String name = CalculatedOperands.GetOperandBasicVal(getM_operand_id());\n"+
	    						"return \"Hello 2 \"+name;\n"+
	    					"}\n"+
	    					
	    				"}\n"+
	    			"}\n"+
	    			"\n";
	    	
//	    	source =
//	    		    "import java.util.Collections;\n"+
//	    		    "public class StringSorterByText {\n"+
//	    		        "public void sort(List<Strings> strings) {\n"+
//	    		            "Collections.sort(strings);\n"+
//	    		        "}\n"+
//	    		    "}";
	    	return source;
	    }

	public static void main(String[] args) {
//		CalculatedOperands.SetOperandsVals();
//		CalculatedOperands.Operand_1 o1 = new CalculatedOperands.Operand_1();
//		CalculatedOperands.Operand_2 o2 = new CalculatedOperands.Operand_2();
//		System.out.println(o1.Calculate());
//		System.out.println(o2.Calculate());
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

		StandardJavaFileManager fileManager =
		compiler.getStandardFileManager(diagnostics, null, null);
		
		//should be platform independent path
		//also the folder should already exist
		try {
			
			StringBuilder sbd = new StringBuilder();
			sbd.append(Constants.FILE_SEPARATOR).append("compiledClasses");
			
			File file = new File(sbd.toString());
			file.mkdirs();
			
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
			        Arrays.asList(file));
			
			JavaCompiler.CompilationTask task = compiler.getTask(null,
			        fileManager, diagnostics, null, null, getCompilationUnits());

			if (!task.call()) {
			    diagnostics.getDiagnostics().forEach(System.out::println);
			}
			fileManager.close();

			ClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()});
			
			//loading and using our compiled class
			Class CalculatedOperands = loader.loadClass("CalculatedOperands");
			CalculatedOperands.getMethod("SetOperandsVals").invoke(null);
			Class<ICalcOperand>[] cl = CalculatedOperands.getClasses();
			
			for (Class<ICalcOperand> co : cl){

				//System.out.println(co.newInstance().Calculate());

			}
			
//			Class<ICalcOperand> Operand_1 = (Class<ICalcOperand>) loader.loadClass("CalculatedOperands.Operand_1");
//			ICalcOperand iOperand_1 = Operand_1.newInstance();
//			System.out.println(iOperand_1.Calculate());
//			
//			Class<ICalcOperand> Operand_2 = (Class<ICalcOperand>) loader.loadClass("CalculatedOperands.Operand_2");
//			ICalcOperand iOperand_2 = Operand_2.newInstance();
//			System.out.println(iOperand_2.Calculate());
		
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

