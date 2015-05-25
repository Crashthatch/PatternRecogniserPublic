package database.features;

import database.Att;
import database.MetaModelDatabase;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class AttFeatureSet extends AbstractMap<String, Object> {
	
	private Att att;
	private int notNullRowsInTable;
	private int uniqueRowsInTable;
	private int numRowsInTable;
	private int nullRowsInTable;
	private int numAncestorAtts;
	private int depth;
	private String guessedType;
    private boolean isDuplicate;
	private boolean isAlmostDuplicate;
	private int bestTargetAttRowsCovered; 
	private double bestPercentageOfTargetAttCovered; 
	private int bestTargetAttTotalLength;
	private int bestTargetAttRows;
	private double bestAimForScore;
	private int totalLength;
	private int firstRowLength;
	private double averageRowLength;
	private int numParents;
	private boolean generatedByConstantCreator;
    private int bestCoveringRows;
    private double bestCoveringPercentageAvg;
    private int inputToRelationships;
    private int RRAToRelationships;
    private int datatableAttsUsedInRelationships;
    private int datatableAttsUsedAsRRAsInRelationships;
    private boolean createdByModelApplier;

    private static PreparedStatement insertPreparedStatement; //Static so can insert multiple FeatureSets at once, not tied to a specific FeatureSet.
    private static final Set<String> doNotSaveProperties;
    private static final Field[] declaredFields; //Cache for speed.

    static {
        declaredFields = AttFeatureSet.class.getDeclaredFields();
        doNotSaveProperties = new HashSet<>();
        doNotSaveProperties.add("att");
        doNotSaveProperties.add("doNotSaveProperties");
        doNotSaveProperties.add("insertPreparedStatement");
        doNotSaveProperties.add("declaredFields");
    }

	public String toString(){
		String ret = att.getDbColumnName() + " " + att.getName() +" "+this.getClass().getSimpleName()+ ": [ ";
		for( Field field : declaredFields ){
			if( !doNotSaveProperties.contains(field.getName()) ){
				try {
					ret += field.get(this).toString() +" ";
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		ret += "]";
		return ret;
	}

    public Set<Entry<String, Object>> entrySet(){
        Set<Entry<String,Object>> ret = new HashSet<>();

        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ) {
                try {
                    ret.add(new SimpleEntry<String, Object>(field.getName(), field.get(this)));
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    public static String getCreateFieldsSql(String prefix) throws UnknownPropertyTypeException{

        String SQL = "";
        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ){
                SQL += "\n`"+prefix+field.getName()+"` " ;
                if( field.getType().getSimpleName().equals("int") ){
                    SQL += "int(11) DEFAULT NULL,";
                }
                else if( field.getType().getSimpleName().equals("boolean")){
                    SQL += "tinyint(1) DEFAULT NULL,";
                }
                else if( field.getType().getSimpleName().equals("double")){
                    SQL += "double DEFAULT NULL,";
                }
                else if( field.getType().getSimpleName().equals("String")){
                    SQL += "text CHARACTER SET utf8mb4 DEFAULT NULL,";
                }
                else{
                    throw new UnknownPropertyTypeException("Don't know how to create property of type '"+field.getType().getSimpleName()+"' in the Database.");
                }
            }
        }

        return SQL;
    }

    public static String getCreateAttributeTableSql() throws UnknownPropertyTypeException {
        String SQL = "CREATE TABLE IF NOT EXISTS `attribute` (\n" +
                "  `runId` text CHARACTER SET utf8mb4 NOT NULL,\n" +
                "  `processingRound` int(11) DEFAULT NULL,\n" +
                "  `attName` text CHARACTER SET utf8mb4 DEFAULT NULL,\n" +
                "  `attDbCol` text CHARACTER SET utf8mb4 DEFAULT NULL,\n" +
                "  `attId` int(11) NOT NULL,\n" +
                "  `scoreDuringRun` double DEFAULT NULL,\n" +
                "  `rankingDuringRun` int(11) DEFAULT NULL,\n" +
                "  `runFoundPredictor` tinyint(1) NOT NULL DEFAULT '0',\n" +
                "  `usedToMakeFinalPrediction` tinyint(1) NOT NULL DEFAULT '0',";
        SQL += getCreateFieldsSql("");

        //SQL = SQL.substring(0, SQL.length()-1);   //Remove final comma & newline.
        SQL += "\n KEY `runId` (`runId`(100),`processingRound`,`attId`)";
        SQL += "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        return SQL;
    }

    private static PreparedStatement getOrCreateInsertPreparedStatement(){
        try {
            if( insertPreparedStatement != null && !insertPreparedStatement.isClosed() ){
                return insertPreparedStatement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> fieldNames = getFieldNames("");

        String SQL = "INSERT INTO `attribute` (`runId`, `processingRound`, `attName`, `attDbCol`, `attId`, `" + StringUtils.join(fieldNames, "`,`") + "`) " +
                "VALUES ( ";
        for( int i=fieldNames.size()+5; i > 0; i-- ){
            SQL += " ?,";
        }
        SQL = SQL.substring(0, SQL.length()-1); //Remove final ,
        SQL += ")";

        System.out.println(SQL);

        try {
            insertPreparedStatement = MetaModelDatabase.getConnection().prepareStatement(SQL);
        }
        catch( SQLException e){
            System.out.println(e);
        }
        return insertPreparedStatement;
    }

    public static ArrayList<String> getFieldNames(String prefix) {
        ArrayList<String> fieldNames = new ArrayList<>();
        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ){
                fieldNames.add(prefix+field.getName());
            }
        }
        return fieldNames;
    }

    public void addToBatchInsert(String runId, int processingRound) throws SQLException{

        PreparedStatement stmt = getOrCreateInsertPreparedStatement();

        ArrayList<String> fieldValues = new ArrayList<>();
        stmt.setString(1, runId);
        stmt.setInt(2, processingRound);
        stmt.setString(3, att.getName());
        stmt.setString(4, att.getDbColumnNameNoQuotes());
        stmt.setInt(5, att.getAttOrder());
        int fieldKey = 6;
        setStmtStrings(stmt, fieldKey);
        stmt.addBatch();
	}

    /**
     * Adds the features to the given statement, starting at the given index.
     * Returns the integer fieldKey that is the next unset value.
     * (ie. if you pass 3 and there are 3 fields, it will assign strings to fields 3,4 and 5, and return 6).
     * @param stmt
     * @param fieldKey
     * @return
     * @throws SQLException
     */
    public int setStmtStrings(PreparedStatement stmt, int fieldKey) throws SQLException {
        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ){
                try {
                    if( field.getType().getSimpleName().equals("boolean") ) {
                        stmt.setString(fieldKey, ((boolean)field.get(this))?"1":"0");
                    }
                    else if( field.getType().getSimpleName().equals("double") ){
                        stmt.setDouble(fieldKey, Math.round(((double)((double)field.get(this))*100000))/100000 );
                    }
                    else{
                        stmt.setString(fieldKey, field.get(this).toString() );
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fieldKey++;
            }
        }
        return fieldKey;
    }

    public static void saveBatch()  throws SQLException{
        PreparedStatement stmt = getOrCreateInsertPreparedStatement();
        stmt.executeBatch();
    }
	
	
	public AttFeatureSet(Att att) {
        this.att = att;
    }
	
	public Object get(String key){
		for( Field field : declaredFields ){
			if( field.getName().equals(key) ){
				try {
					return field.get(this);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Att getAtt() {
		return att;
	}
	public int getNotNullRowsInTable() {
		return notNullRowsInTable;
	}
	public int getUniqueRowsInTable() {
		return uniqueRowsInTable;
	}
	public int getNullRowsInTable() {
		return nullRowsInTable;
	}
	public int getNumRowsInTable() {
		return numRowsInTable;
	}
	public int getNumAncestorAtts() {
		return numAncestorAtts;
	}
	public int getDepth() {
		return depth;
	}
	public String getGuessedType() {
		return guessedType;
	}
	public boolean isDuplicate() {
		return isDuplicate;
	}
	public boolean isAlmostDuplicate() {
		return isAlmostDuplicate;
	}
	public double getBestAimForScore() {
		return bestAimForScore;
	}
	public int getBestTargetAttRows(){
		return bestTargetAttRows;
	}
	public int getBestTargetAttRowsCovered() {
		return bestTargetAttRowsCovered;
	}
	public double getBestPercentageOfTargetAttCovered() {
		return bestPercentageOfTargetAttCovered;
	}
	public int getBestTargetAttTotalLength() {
		return bestTargetAttTotalLength;
	}
	public int getTotalLength() {
		return totalLength;
	}
	public int getFirstRowLength() {
		return firstRowLength;
	}
	public double getAverageRowLength() {
		return averageRowLength;
	}
	public int getNumParents() {
		return numParents;
	}
    public boolean getCreatedByModelApplier(){ return createdByModelApplier; }
	public boolean generatedByConstantCreator() {
		return generatedByConstantCreator;
	}
    public int getRRAToRelationships() { return RRAToRelationships; }
    public void setRRAToRelationships(int RRAToRelationships) { this.RRAToRelationships = RRAToRelationships; }
    public int getInputToRelationships() { return inputToRelationships; }
    public void setInputToRelationships(int inputToRelationships) { this.inputToRelationships = inputToRelationships; }
    public int getDatatableAttsUsedInRelationships() { return datatableAttsUsedInRelationships; }
    public void setDatatableAttsUsedInRelationships(int datatableAttsUsedInRelationships) { this.datatableAttsUsedInRelationships = datatableAttsUsedInRelationships; }
    public int getDatatableAttsUsedAsRRAsInRelationships() { return datatableAttsUsedAsRRAsInRelationships; }
    public void setDatatableAttsUsedAsRRAsInRelationships(int datatableAttsUsedAsRRAsInRelationships) { this.datatableAttsUsedAsRRAsInRelationships = datatableAttsUsedAsRRAsInRelationships; }

    public void setNotNullRowsInTable(int notNullRowsInTable) {
        this.notNullRowsInTable = notNullRowsInTable;
    }
    public void setUniqueRowsInTable(int uniqueRowsInTable) {
        this.uniqueRowsInTable = uniqueRowsInTable;
    }
    public void setNumRowsInTable(int numRowsInTable) {
        this.numRowsInTable = numRowsInTable;
    }
    public void setNullRowsInTable(int nullRowsInTable) {
        this.nullRowsInTable = nullRowsInTable;
    }
    public void setNumAncestorAtts(int numAncestorAtts) {
        this.numAncestorAtts = numAncestorAtts;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setGuessedType(String guessedType) {
        this.guessedType = guessedType;
    }

    public void setDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public void setAlmostDuplicate(boolean isAlmostDuplicate) {
        this.isAlmostDuplicate = isAlmostDuplicate;
    }

    public void setBestTargetAttRowsCovered(int bestTargetAttRowsCovered) {
        this.bestTargetAttRowsCovered = bestTargetAttRowsCovered;
    }

    public void setBestPercentageOfTargetAttCovered(double bestPercentageOfTargetAttCovered) {
        this.bestPercentageOfTargetAttCovered = bestPercentageOfTargetAttCovered;
    }

    public void setBestTargetAttTotalLength(int bestTargetAttTotalLength) {
        this.bestTargetAttTotalLength = bestTargetAttTotalLength;
    }

    public void setBestTargetAttRows(int bestTargetAttRows) {
        this.bestTargetAttRows = bestTargetAttRows;
    }

    public void setBestAimForScore(double bestAimForScore) {
        this.bestAimForScore = bestAimForScore;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public void setFirstRowLength(int firstRowLength) {
        this.firstRowLength = firstRowLength;
    }

    public void setAverageRowLength(double averageRowLength) {
        this.averageRowLength = averageRowLength;
    }

    public void setNumParents(int numParents) {
        this.numParents = numParents;
    }

    public void setGeneratedByConstantCreator(boolean generatedByConstantCreator) {
        this.generatedByConstantCreator = generatedByConstantCreator;
    }

    public void setBestCoveringRows(int bestCoveringRows) {
        this.bestCoveringRows = bestCoveringRows;
    }

    public void setBestCoveringPercentageAvg(double bestCoveringPercentageAvg) {
        this.bestCoveringPercentageAvg = bestCoveringPercentageAvg;
    }

    public void setCreatedByModelApplier(boolean createdByModelApplier) {
        this.createdByModelApplier = createdByModelApplier;
    }

    public int hashCode() {
        final int prime = 53;
        int result = prime * att.hashCode();
        result += prime * entrySet().hashCode();
        return result;
    }

    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        AttFeatureSet other = (AttFeatureSet) obj;
        if( !att.equals(other.getAtt()) )
            return false;

        if( !this.entrySet().equals(other.entrySet() ) )
            return false;

        return true;
    }


}
