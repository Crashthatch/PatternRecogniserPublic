package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class getInputFromFile extends TransformationService {
	private String filename;
	
	public getInputFromFile( String filename )
	{
		colsIn = 0;
		colsOut = 1;
		rowsIn = 0;
		rowsOut = 1;
		this.filename = filename;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
				
		try {
			String fileAsString = FileUtils.readFileToString(new File(filename));
			
			outTable.put(0, 0, fileAsString);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
		
		return outTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		getInputFromFile other = (getInputFromFile) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}
}
