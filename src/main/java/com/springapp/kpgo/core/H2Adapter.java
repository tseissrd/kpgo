/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.core;

import java.lang.reflect.Constructor;
import java.sql.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author Anna
 */
public class H2Adapter {
    
    public JdbcTemplate jdbct;
    
    public H2Adapter() {
        try {
            // Class dsClass = Class.forName("org.h2.jdbcx.JdbcDataSource");
            // Constructor dsConstructor = dsClass.getDeclaredConstructor();
            // jdbct = new JdbcTemplate((DataSource)dsConstructor.newInstance());
            JdbcDataSource ds = new JdbcdataSource();
            jdbct = new JdbcTemplate(JdbcDataSource);
        } catch (Exception err) {
            throw new Error(err);
        }
    }
    
    public void test() {
        jdbct.execute("CREATE TABLE test_table");
    }
    
}
