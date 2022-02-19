/*
  User: Cloudy
  Date: 07/01/2022
  Time: 03:39
*/

package cz.cloudy.minecraft.core.database.enums;

/**
 * @author Cloudy
 */
public enum QueryType {
    /**
     * Raw DQL.
     */
    RawDQL,
    /**
     * Raw DML or DDL.
     */
    RawDMLOrDDL,
    /**
     * Select.
     */
    Select,
    /**
     * Insert.
     */
    Insert,
    /**
     * Update.
     */
    Update,
    /**
     * Delete.
     */
    Delete
}
