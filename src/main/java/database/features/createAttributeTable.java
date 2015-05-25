package database.features;

import database.MetaModelDatabase;

import java.sql.SQLException;

/**
 * Created by Tom on 16/05/2014.
 */
public class createAttributeTable {

    public static void main(String[] args) {
        try {
            String SQL = AttFeatureSet.getCreateAttributeTableSql();
            System.out.println(SQL);

            MetaModelDatabase.reconnect("pattern_metamodel");
            MetaModelDatabase.doWriteQuery(SQL);

        }catch (UnknownPropertyTypeException | SQLException e){
            e.printStackTrace();
        }
    }

}
