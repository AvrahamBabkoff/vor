package il.co.vor.SystemManager;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.Toolkit;

public class NumericTextField  extends JTextField
{

	private static final long serialVersionUID = 1L;
    /**
    * This constructor takes the length of the textfield, and the max allowed integer value.
    *
    * @param int cols length of the textfield.
    * @param int _maxValue max allowed integer value.
    */
    public NumericTextField(int cols, int _maxValue) 
    {
        super(cols);
        setDocument(new NumericDocument(_maxValue));
    }


	private static class NumericDocument extends PlainDocument 
	{
		private static final long serialVersionUID = 1L;
		private int maxValue;
	    /**
	    * This constructor takes the max allowed integer value.
	    *
	    * @param int _maxValue max integer value.
	    */
	    public NumericDocument(int _maxValue) 
	    {
	        super();
	        maxValue = _maxValue;
	        if (maxValue < 0)
	        {
	        	maxValue = Integer.MAX_VALUE;
	        }
	    }
	    
	    @Override
	    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException 
	    {
	        char[] insertChars = str.toCharArray();
	        boolean valid = true;
	        int iPort;
	        String sOld = getText(0, getLength());
	        String sNew;
            for(int i = 0; i < insertChars.length; i++) 
            {
                if(!Character.isDigit(insertChars[i])) 
                {
                    valid = false;
                    break;
                }
            }
	            
	        if (valid)
	        {
	            super.insertString(offset,str,a);
	            // check that we did not exceed the allowed max value
	            sNew = getText(0, getLength());
	            iPort = Integer.parseInt(sNew);
	            if (iPort >  maxValue)
	            {
	            	super.remove(0, getLength());
	            	super.insertString(0,sOld,a);
	            	valid = false;
	            }
	        }
	        if (!valid)
	        {
	        	Toolkit.getDefaultToolkit().beep();
	        }
	    }
	}
}
