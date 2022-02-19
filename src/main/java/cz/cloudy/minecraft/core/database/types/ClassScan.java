/*
  User: Cloudy
  Date: 07/01/2022
  Time: 21:57
*/

package cz.cloudy.minecraft.core.database.types;

import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.annotation.Table;

/**
 * @author Cloudy
 */
public class ClassScan {
    private final Class<? extends DatabaseEntity> clazz;
    private final Table                           table;

    private boolean constructed = false;

    /**
     * Default constructor for database entity class scan
     *
     * @param clazz Database entity type
     * @param table Table annotation
     */
    public ClassScan(Class<? extends DatabaseEntity> clazz, Table table) {
        this.clazz = clazz;
        this.table = table;
    }

    /**
     * Getter for database entity type
     *
     * @return Database entity type
     */
    public Class<? extends DatabaseEntity> clazz() {
        return clazz;
    }

    /**
     * Getter for {@link Table} annotation
     *
     * @return Table annotation
     */
    public Table table() {
        return table;
    }

    /**
     * Getter if this class scan was constructed in the database
     *
     * @return True if was constructed
     */
    public boolean isConstructed() {
        return constructed;
    }

    /**
     * Setter if this class scan was constructed in the database
     *
     * @param constructed Constructed flag
     */
    public void setConstructed(boolean constructed) {
        this.constructed = constructed;
    }
}
