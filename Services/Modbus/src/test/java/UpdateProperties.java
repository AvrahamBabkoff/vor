import java.util.ArrayList;

import il.co.vor.DalDataClient.DalDataClient;
import il.co.vor.API.NPLService.*;
import il.co.vor.DalDataObjects.CalcOperandProperty;
import il.co.vor.DalDataObjects.FtpFile;
import il.co.vor.NPLClient.NPLClient;

public class UpdateProperties {

	public static void main(String[] args) {
		
		ArrayList<CalcOperandProperty> _propertiesList = new ArrayList<CalcOperandProperty>();
		
		CalcOperandProperty pr = null;
		
		pr = new CalcOperandProperty();
		pr.setPropertyName("Test");
		pr.setPropertyType(3);
		pr.setPropertyVal("6");
		pr.setPropertyDescription("Test");
		
		_propertiesList.add(pr);
		
		pr = new CalcOperandProperty();
		pr.setPropertyName("Basic_Manual");
		pr.setPropertyType(3);
		pr.setPropertyVal("66");
		pr.setPropertyDescription("Test1");
	
		_propertiesList.add(pr);
		
		
		
//		pr = new CalcOperandProperty();
//		pr.setPropertyName("T2_Set_Point_Up1");
//		pr.setPropertyType(3);
//		pr.setPropertyVal("7");
//		pr.setPropertyDescription("T2_Set_Point_Up1");
//		_propertiesList.add(pr);
//		
//		pr = new CalcOperandProperty();
//		pr.setPropertyName("T2_Set_Point_Up2");
//		pr.setPropertyType(3);
//		pr.setPropertyVal("8.5");
//		pr.setPropertyDescription("T2_Set_Point_Up2");
//		_propertiesList.add(pr);
//		
//		pr = new CalcOperandProperty();
//		pr.setPropertyName("T2_Set_Point_Up3");
//		pr.setPropertyType(3);
//		pr.setPropertyVal("10");
//		pr.setPropertyDescription("T2_Set_Point_Up3");
//		_propertiesList.add(pr);
		
		DalDataClient dcc = new DalDataClient("localhost", 8082);
		dcc.getCalcOperandsProperties().updateCalcOperandProperties(9, _propertiesList);
		
		NPLClient nplc = new NPLClient("localhost", 8085);
		nplc.getOperands().ReloadCalcOperandsProperties();
	}

}
