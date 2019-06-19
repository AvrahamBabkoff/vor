package il.co.vor.ApiObjectsCommon;

import javax.xml.bind.annotation.XmlElement;

import il.co.vor.common.VosDataResources;

public class NumberOfAffectedRows {
	private int m_number_of_affected_rows;

	@XmlElement(name=VosDataResources.NUMBER_OF_AFFECTED_ROWS_NAME)
	public int getNumberOfAffectedRows() {
		return m_number_of_affected_rows;
	}

	public void setNumberOfAffectedRows(int _number_of_affected_rows) {
		this.m_number_of_affected_rows = _number_of_affected_rows;
	}
}
