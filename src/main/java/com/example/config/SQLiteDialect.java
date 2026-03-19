package com.example.config;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.dialect.sequence.NoSequenceSupport;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

/**
 * SQLiteDialect for Hibernate 7.x (Spring Boot 4.x)
 *
 * Hibernate 7 auto-generates correct LIMIT/OFFSET for SQLite when you
 * extend the base Dialect and return the right DatabaseVersion.
 * The old LimitHandler / AbstractLimitHandler API no longer exists.
 */
public class SQLiteDialect extends Dialect {

    private static final DatabaseVersion MINIMUM_VERSION = DatabaseVersion.make(3);

    public SQLiteDialect() {
        super(MINIMUM_VERSION);
    }

    public SQLiteDialect(DialectResolutionInfo info) {
        super(info);
    }

    // SQLite has no sequence support
    @Override
    public SequenceSupport getSequenceSupport() {
        return NoSequenceSupport.INSTANCE;
    }

    // SQLite does not support ALTER TABLE constraints
    @Override
    public boolean hasAlterTable() {
        return false;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getDropForeignKeyString() {
        return "";
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName,
            String[] foreignKey, String referencedTable,
            String[] primaryKey, boolean referencesPrimaryKey) {
        return "";
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return "";
    }
}
