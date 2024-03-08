package com.ly723.db.helper;

import android.content.ContentValues;
import android.text.TextUtils;

import com.ly723.db.interfaces.Column;
import com.ly723.db.interfaces.Table;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class SqlHelper {

    /**
     * according to the Class annotations to genarate sql of create table
     *
     * @return sql to create table
     */
    public static String getCreateTableSQL(Class<?> clazz) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ");
        Table table = clazz.getAnnotation(Table.class);
        String tableName = TextUtils.isEmpty(table.name()) ? clazz.getSimpleName() : table.name();
        sqlBuilder.append(tableName);
        sqlBuilder.append("(");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }

            String columnName = TextUtils.isEmpty(column.name()) ? field.getName() : column.name();
            sqlBuilder.append(columnName).append(" ");

            String columnType = TextUtils.isEmpty(column.type()) ? convertType2Sql(field) : column.type();
            sqlBuilder.append(columnType).append(" ");

            if (!column.isNull()) {
                sqlBuilder.append(" NOT NULL ");
            }
            if (column.isPrimaryKey()) {
                sqlBuilder.append(" PRIMARY KEY ");
            }

            if (column.isUnique()) {
                sqlBuilder.append(" UNIQUE ");
            }

            if (!column.defaultValue().equals("null")) {
                sqlBuilder.append(" DEFAULT ").append(column.defaultValue());
            }
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    private static String convertType2Sql(Field field) {
        Class clazz = field.getType();
        String type = "";
        if (clazz.isInstance(int.class) || clazz.isInstance(Integer.class)) {
            type = "integer";
        } else if (clazz.isInstance(long.class) || clazz.isInstance(Long.class)) {
            type = "numeric";
        } else if (clazz.isInstance(String.class)) {
            type = "text";
        } else if (clazz.isInstance(Date.class)) {
            type = "text";
        } else if (clazz.isInstance(boolean.class) || clazz.isInstance(Boolean.class)) {
            type = "integer";
        } else if (clazz.isInstance(float.class) || clazz.isInstance(Float.class)) {
            type = "real";
        } else if (clazz.isInstance(double.class) || clazz.isInstance(Double.class)) {
            type = "real";
        } else if (clazz.isInstance(Blob.class)) {
            type = "blob";
        }
        return type;
    }

    /**
     * get table name
     *
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        return clazz.getSimpleName();
    }

    /**
     * return table version
     *
     * @param clazz
     * @return
     */
    public static int getTableVersion(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        return table.version();
    }

    /**
     * return info about table's all columns
     *
     * @param clazz
     * @return
     */
    public static List<ColumnInfo> getTableColumnInfos(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
        for (Field field : fields) {
            if (!field.isAccessible())
                field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setName(field.getName());
            columnInfo.setType(convertType2Sql(field));
            columnInfo.setNull(column.isNull());
            columnInfo.setPrimaryKey(column.isPrimaryKey());
            columnInfo.setUnique(column.isUnique());
            columnInfo.setDefaultValue(column.defaultValue());
            columnInfos.add(columnInfo);
        }
        return columnInfos;
    }

    /**
     * return sql of add a columm to table
     */
    public static String getAddColumnSql(String table, ColumnInfo columnInfo) {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append(String.format("ALTER TABLE %s ADD %s %s ", table, columnInfo.getName(), columnInfo.getType()));
        if (!columnInfo.isNull()) {
            sbSql.append(" NOT NULL ");
        }
        if (columnInfo.isPrimaryKey()) {
            sbSql.append(" PRIMARY KEY ");
        }

        if (columnInfo.isUnique()) {
            sbSql.append(" UNIQUE ");
        }

        if (!columnInfo.getDefaultValue().equals("null")) {
            sbSql.append(" DEFAULT ").append(columnInfo.getDefaultValue());
        }

        sbSql.append(";");

        return sbSql.toString();
    }

    /**
     * get primary key
     */
    public static String getPrimaryKey(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;
            if (column.isPrimaryKey()) {
                return field.getName();
            }
        }
        return null;
    }

    /**
     * use reflection to parse model's value to contentValues
     */
    public static void parseModelToContentValues(Object model,
                                                 ContentValues contentValues) {
        if (contentValues.size() > 0)
            contentValues.clear();

        Class<?> clazz = model.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Class<?> fieldType = null;
        Object fieldVal = null;

        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Column column = field.getAnnotation(Column.class);
                fieldType = field.getType();
                fieldVal = field.get(model);
                if (column == null || fieldVal == null)
                    continue;

                if (fieldType.equals(int.class)) {
                    contentValues.put(field.getName(), field.getInt(model));
                } else if (fieldType.equals(Integer.class)) {
                    contentValues.put(field.getName(), (Integer) field.get(model));
                } else if (fieldType.equals(short.class)) {
                    contentValues.put(field.getName(), field.getShort(model));
                } else if (fieldType.equals(Short.class)) {
                    contentValues.put(field.getName(), (Short) field.get(model));
                } else if (fieldType.equals(long.class)) {
                    contentValues.put(field.getName(), field.getLong(model));
                } else if (fieldType.equals(Long.class)) {
                    contentValues.put(field.getName(), (Long) field.get(model));
                } else if (fieldType.equals(float.class)) {
                    contentValues.put(field.getName(), field.getFloat(model));
                } else if (fieldType.equals(Float.class)) {
                    contentValues.put(field.getName(), (Float) field.get(model));
                } else if (fieldType.equals(double.class)) {
                    contentValues.put(field.getName(), field.getDouble(model));
                } else if (fieldType.equals(Double.class)) {
                    contentValues.put(field.getName(), (Double) field.get(model));
                } else if (fieldType.equals(boolean.class)) {
                    if (field.getBoolean(model)) {
                        contentValues.put(field.getName(), "1");
                    } else {
                        contentValues.put(field.getName(), "0");
                    }
                } else if (fieldType.equals(Boolean.class)) {
                    if ((Boolean) field.get(model)) {
                        contentValues.put(field.getName(), "1");
                    } else {
                        contentValues.put(field.getName(), "0");
                    }
                } else if (fieldType.equals(String.class)) {
                    contentValues.put(field.getName(), (String) field.get(model));
                } else if (fieldType.equals(byte[].class)) {
                    contentValues.put(field.getName(), (byte[]) field.get(model));
                } else if (fieldType.equals(Date.class)) {
                    Date date = (Date) field.get(model);
                    contentValues.put(field.getName(), DateUtils.formatDate2Str(date));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * use reflection to parse queryResult's value into model
     */
    public static void parseResultSetToModel(ResultSet queryResult,
                                             Object model) {
        Class<?> clazz = model.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Object fieldVal;
        Class<?> fieldType;
        try {
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Column column = field.getAnnotation(Column.class);
                if (column == null)
                    continue;
                String columnName = field.getName();
                fieldVal = queryResult.getValue(columnName);
                fieldType = field.getType();
                if (fieldVal != null) {
                    if (fieldType.equals(fieldVal.getClass())) {
                        field.set(model, fieldVal);
                    } else if (fieldType.equals(short.class)) {
                        field.setShort(model, queryResult.getShortValue(columnName));
                    } else if (fieldType.equals(Short.class)) {
                        field.set(model, queryResult.getShortValue(columnName));
                    } else if (fieldType.equals(int.class)) {
                        field.setInt(model, queryResult.getIntValue(columnName));
                    } else if (fieldType.equals(Integer.class)) {
                        field.set(model, queryResult.getIntValue(columnName));
                    } else if (fieldType.equals(long.class)) {
                        field.setLong(model,
                                queryResult.getLongValue(columnName));
                    } else if (fieldType.equals(Long.class)) {
                        field.set(model, queryResult
                                .getLongValue(columnName));
                    } else if (fieldType.equals(float.class)) {
                        field.setFloat(model,
                                queryResult.getFloatValue(columnName));
                    } else if (fieldType.equals(Float.class)) {
                        field.set(model, queryResult
                                .getFloatValue(columnName));
                    } else if (fieldType.equals(double.class)) {
                        field.setDouble(model,
                                queryResult.getDoubleValue(columnName));
                    } else if (fieldType.equals(Double.class)) {
                        field.set(model, queryResult
                                .getDoubleValue(columnName));
                    } else if (fieldType.equals(boolean.class)) {
                        field.setBoolean(model,
                                queryResult.getBooleanValue(columnName));
                    } else if (fieldType.equals(Boolean.class)) {
                        field.set(model, queryResult
                                .getBooleanValue(columnName));
                    } else if (fieldType.equals(String.class)) {
                        field.set(model, queryResult.getStringValue(columnName));
                    } else if (fieldType.equals(Date.class)) {
                        field.set(model, queryResult.getDateValue(columnName));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void parseResultSetListToModelList(
            List<ResultSet> queryResultList, List mList, Class<?> mdlType) {
        try {
            if (queryResultList == null || queryResultList.isEmpty())
                return;
            for (ResultSet queryResult : queryResultList) {
                Object model = mdlType.newInstance();
                parseResultSetToModel(queryResult, model);
                mList.add(model);
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

}
