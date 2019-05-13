/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.parse;

import org.apache.shardingsphere.core.parse.antlr.sql.statement.SQLStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.SetStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.DescribeStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowColumnsStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowCreateTableStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowDatabasesStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowIndexStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowOtherStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowTableStatusStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.ShowTablesStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dal.dialect.mysql.statement.UseStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dml.DMLStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dml.DQLStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.tcl.TCLStatement;
import org.apache.shardingsphere.core.parse.old.parser.exception.SQLParsingException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class SQLJudgeEngineTest {
    
    @Test
    public void assertJudgeForSelect() {
        assertThat(new SQLJudgeEngine(" /*COMMENT*/  \t \n  \r \fsElecT\t\n  * from table  ").judge(), instanceOf(DQLStatement.class));
    }
    
    @Test
    public void assertJudgeForInsert() {
        assertThat(new SQLJudgeEngine(" - - COMMENT  \t \n  \r \finsert\t\n  into table  ").judge(), instanceOf(InsertStatement.class));
    }
    
    @Test
    public void assertJudgeForUpdate() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fuPdAte\t\n  table  ").judge(), instanceOf(DMLStatement.class));
    }
    
    @Test
    public void assertJudgeForDelete() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fdelete\t\n  table  ").judge(), instanceOf(DMLStatement.class));
    }
    
    @Test
    public void assertJudgeForSetTransaction() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fset\t\n  transaction  ").judge(), instanceOf(TCLStatement.class));
    }
    
    @Test
    public void assertJudgeForSetAutoCommit() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fset\t\n  autocommit  ").judge(), instanceOf(TCLStatement.class));
    }
    
    @Test
    public void assertJudgeForSetOther() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fset\t\n  other  ").judge(), instanceOf(SetStatement.class));
    }
    
    @Test
    public void assertJudgeForCommit() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fcommit  ").judge(), instanceOf(TCLStatement.class));
    }
    
    @Test
    public void assertJudgeForRollback() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \frollback  ").judge(), instanceOf(TCLStatement.class));
    }
    
    @Test
    public void assertJudgeForSavePoint() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fSavePoint  ").judge(), instanceOf(TCLStatement.class));
    }
    
    @Test
    public void assertJudgeForBegin() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fbegin  ").judge(), instanceOf(TCLStatement.class));
    }
    
    @Test
    public void assertJudgeForUse() {
        SQLStatement statement = new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fuse sharding_db  ").judge();
        assertThat(statement, instanceOf(UseStatement.class));
        assertThat(((UseStatement) statement).getSchema(), is("sharding_db"));
    }
    
    @Test
    public void assertJudgeForDescribe() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fdescribe t_order  ").judge(), instanceOf(DescribeStatement.class));
    }
    
    @Test
    public void assertJudgeForDesc() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fdesc t_order  ").judge(), instanceOf(DescribeStatement.class));
    }
    
    @Test
    public void assertJudgeForShowDatabases() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow databases  ").judge(), instanceOf(ShowDatabasesStatement.class));
    }
    
    @Test
    public void assertJudgeForShowTableStatus() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow table status from logic_db").judge(), instanceOf(ShowTableStatusStatement.class));
    }
    
    @Test
    public void assertJudgeForShowTables() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow tables  ").judge(), instanceOf(ShowTablesStatement.class));
    }
    
    @Test
    public void assertJudgeForShowColumns() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow columns from t_order ").judge(), instanceOf(ShowColumnsStatement.class));
    }
    
    @Test
    public void assertJudgeForShowIndex() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow index from t_order ").judge(), instanceOf(ShowIndexStatement.class));
    }
    
    @Test
    public void assertJudgeForShowCreateTable() {
        SQLStatement sqlStatement = new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow create table logic_db.t_order  ").judge();
        assertThat(sqlStatement, instanceOf(ShowCreateTableStatement.class));
        assertThat(sqlStatement.getSQLTokens().size(), is(1));
    }
    
    @Test
    public void assertJudgeForShowOthers() {
        assertThat(new SQLJudgeEngine(" /*+ HINT SELECT * FROM TT*/  \t \n  \r \fshow session ").judge(), instanceOf(ShowOtherStatement.class));
    }
    
    @Test
    public void assertJudgeForCall() {
        assertThat(new SQLJudgeEngine("call test_procedure()").judge(), instanceOf(DQLStatement.class));
    }
    
    @Test(expected = SQLParsingException.class)
    public void assertJudgeForInvalidSQL() {
        new SQLJudgeEngine("int i = 0").judge();
    }
}
