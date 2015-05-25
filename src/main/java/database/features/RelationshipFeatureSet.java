package database.features;

import database.AttRelationshipGraph;
import database.MetaModelDatabase;
import database.Relationship;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RelationshipFeatureSet extends AbstractMap<String, Object> {

    private Relationship relationship;
	private AttFeatureSet labelFeatures;
    private AttFeatureSet rraFeatures;
    private AttFeatureSet firstInputFeatures; //This will need extending somehow when we are dealing with relationships with multiple inputs
    private String modelLearnerName;
    private boolean labelHashEqualsFirstInputHash;
    private double scoreDuringRun;

    private static PreparedStatement insertPreparedStatement; //Static so can insert multiple FeatureSets at once, not tied to a specific FeatureSet.
    private static final Set<String> doNotSaveProperties;
    private static final Field[] declaredFields; //Cache for speed.

    static {
        declaredFields = RelationshipFeatureSet.class.getDeclaredFields();
        doNotSaveProperties = new HashSet<>();
        doNotSaveProperties.add("relationship");
        doNotSaveProperties.add("doNotSaveProperties");
        doNotSaveProperties.add("insertPreparedStatement");

        //The AttFeatures aren't saved as single fields, so add them to the doNotSaveProperties.
        doNotSaveProperties.add("labelFeatures");
        doNotSaveProperties.add("rraFeatures");
        doNotSaveProperties.add("firstInputFeatures");

        doNotSaveProperties.add("declaredFields");
    }

	public String toString(){
		String ret = relationship.toString() +" "+this.getClass().getSimpleName()+ ": [ ";
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
        Set<Entry<String,Object>> ret = new LinkedHashSet<>();

        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ) {
                try {
                    ret.add(new SimpleEntry<>(field.getName(), field.get(this)));
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        for( Entry entry : labelFeatures.entrySet() ){
            ret.add( new SimpleEntry<String, Object>("label_"+entry.getKey(), entry.getValue()));
        }
        for( Entry entry : rraFeatures.entrySet() ){
            ret.add( new SimpleEntry<String, Object>("rra_"+entry.getKey(), entry.getValue()));
        }
        if( this.firstInputFeatures != null ) {
            for (Entry entry : firstInputFeatures.entrySet()) {
                ret.add(new SimpleEntry<String, Object>("firstInput_" + entry.getKey(), entry.getValue()));
            }
        }
        else{
            //Rel doesn't have a first-input, so just copy the RRA features but replace all values with -1.
            for (Entry entry : rraFeatures.entrySet()) {
                try {
                    if (entry.getValue().getClass().equals(Integer.class)) {
                        ret.add(new SimpleEntry<String, Object>("firstInput_" + entry.getKey(), null));
                    } else if (entry.getValue().getClass().equals(Double.class)) {
                        ret.add(new SimpleEntry<String, Object>("firstInput_" + entry.getKey(), null));
                    } else if (entry.getValue().getClass().equals(Boolean.class)) {
                        ret.add(new SimpleEntry<String, Object>("firstInput_" + entry.getKey(), null));
                    } else if (entry.getValue().getClass().equals(String.class)) {
                        ret.add(new SimpleEntry<String, Object>("firstInput_" + entry.getKey(), null));
                    } else {
                        throw new UnknownPropertyTypeException("Don't know how to create property of type '" + entry.getValue().getClass().getSimpleName() + "' in a RM example row.");
                    }
                }
                catch (UnknownPropertyTypeException e){
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    public static String getCreateTableSql() throws UnknownPropertyTypeException {
        String SQL = "CREATE TABLE IF NOT EXISTS `relationship` (\n" +
                "  `runId` text CHARACTER SET utf8mb4 NOT NULL,\n" +
                "  `processingRound` int(11) DEFAULT NULL,\n" +
                "  `relName` text CHARACTER SET utf8mb4 DEFAULT NULL,\n" +
                "  `relId` int(11) NOT NULL,\n" +
                "  `rankingDuringRun` int(11) DEFAULT NULL,\n" +
                "  `runFoundPredictor` tinyint(1) NOT NULL DEFAULT '0',\n" +
                "  `relationshipCorrectPredictions` int(11) NOT NULL DEFAULT '0',\n" +
                "  `relationshipIncorrectPredictions` int(11) NOT NULL DEFAULT '0',\n" +
                "  `relationshipMadeAllCorrectPredictions` tinyint(1) NOT NULL DEFAULT '0',\n" +
                "  `usedToMakeFinalPrediction` tinyint(1) NOT NULL DEFAULT '0',";

        SQL += AttFeatureSet.getCreateFieldsSql("label_");
        SQL += AttFeatureSet.getCreateFieldsSql("rra_");
        SQL += AttFeatureSet.getCreateFieldsSql("firstInput_");

        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ){
                SQL += "\n`"+field.getName()+"` " ;
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
        //SQL = SQL.substring(0, SQL.length()-1);   //Remove final comma & newline.
        SQL += "\n KEY `runId` (`runId`(100),`relId`)";
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

        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.addAll( AttFeatureSet.getFieldNames("label_") );
        fieldNames.addAll( AttFeatureSet.getFieldNames("rra_") );
        fieldNames.addAll(AttFeatureSet.getFieldNames("firstInput_"));

        for( Field field : declaredFields ){
            if( !doNotSaveProperties.contains(field.getName()) ){
                fieldNames.add(field.getName());
            }
        }

        String SQL = "INSERT INTO `relationship` (`runId`, `processingRound`, `relName`, `relId`, `" + StringUtils.join(fieldNames, "`,`") + "`) " +
                "VALUES ( ";
        for( int i=fieldNames.size()+4; i > 0; i-- ){
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

	public void addToBatchInsert(String runId, int processingRound) throws SQLException{

        PreparedStatement stmt = getOrCreateInsertPreparedStatement();

        ArrayList<String> fieldValues = new ArrayList<>();
        stmt.setString(1, runId);
        stmt.setInt(2, processingRound);
        stmt.setString(3, relationship.toString());
        stmt.setInt(4, relationship.hashCode());
        int fieldKey = 5;
        fieldKey = labelFeatures.setStmtStrings(stmt, fieldKey);
        fieldKey = rraFeatures.setStmtStrings(stmt, fieldKey);
        if( this.firstInputFeatures != null ) {
            fieldKey = firstInputFeatures.setStmtStrings(stmt, fieldKey);
        }
        else{ //Put nulls into the table so the row still gets created & is batchable with other rows which WILL have an input att.
            for( int skipFieldKey = 1; skipFieldKey <= AttFeatureSet.getFieldNames("").size(); skipFieldKey++ ){
                stmt.setString(fieldKey, null );
                fieldKey++;
            }
        }
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
        stmt.addBatch();
	}

    public static void saveBatch()  throws SQLException{
        PreparedStatement stmt = getOrCreateInsertPreparedStatement();
        stmt.executeBatch();
    }


	public RelationshipFeatureSet(Relationship relationship, AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph, Collection<Relationship> relationships) {
        this.relationship = relationship;
        AttFeatureSetFactory featureFactory = new AttFeatureSetFactory(inputGraph, outputGraph, relationships);
        featureFactory.setLookAtData(false);
        this.labelFeatures = featureFactory.getFeaturesForAtt(relationship.getLabel());
        this.rraFeatures = featureFactory.getFeaturesForAtt(relationship.getRootRowAtt());
        if( relationship.getInputAtts().size() > 0 ) {
            this.firstInputFeatures = featureFactory.getFeaturesForAtt(relationship.getInputAtts().get(0));
            try {
                this.labelHashEqualsFirstInputHash = relationship.getLabel().getColumnValuesHash().equals(relationship.getInputAtts().get(0).getColumnValuesHash());
            }
            catch( SQLException e ){
                e.printStackTrace();
            }
        }
        this.modelLearnerName = relationship.getName();

    }

    public void setScoreDuringRun(double score){
        scoreDuringRun = score;
    }
    public double getScoreDuringRun(){
        return scoreDuringRun;
    }

    public Relationship getRelationship(){
        return relationship;
    }
	
}
