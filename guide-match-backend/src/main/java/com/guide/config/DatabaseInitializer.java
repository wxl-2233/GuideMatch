package com.guide.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始数据库初始化检查...");
        
        // 检查数据库连接
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("数据库连接正常，检查表是否存在...");
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            return;
        }

        // 检查users表是否存在
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users'", Integer.class);
            if (count != null && count > 0) {
                System.out.println("users表已存在，跳过初始化脚本");
                return;
            }
        } catch (Exception e) {
            System.out.println("检查表是否存在时出错: " + e.getMessage() + "，继续执行初始化脚本");
        }

        // 执行mysql.sql初始化脚本
        try {
            ClassPathResource resource = new ClassPathResource("db/migration/mysql.sql");
            String sqlScript;
            
            try (InputStream inputStream = resource.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                StringBuilder stringBuilder = new StringBuilder();
                char[] buffer = new char[1024];
                int length;
                while ((length = reader.read(buffer)) != -1) {
                    stringBuilder.append(buffer, 0, length);
                }
                sqlScript = stringBuilder.toString();
            }
            
            System.out.println("成功读取mysql.sql文件，长度: " + sqlScript.length() + " 字符");
            
            // 分割SQL语句并执行
            String[] sqlStatements = sqlScript.split(";");
            int executedCount = 0;
            
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty() && !sql.startsWith("--") && !sql.startsWith("SET")) {
                    try {
                        jdbcTemplate.execute(sql);
                        executedCount++;
                        System.out.println("✓ 执行SQL " + executedCount + ": " + sql.substring(0, Math.min(80, sql.length())) + (sql.length() > 80 ? "..." : ""));
                    } catch (Exception e) {
                        System.err.println("✗ SQL执行失败: " + e.getMessage());
                        System.err.println("失败的SQL: " + sql);
                    }
                }
            }
            
            System.out.println("数据库初始化完成！共执行 " + executedCount + " 条SQL语句");
        } catch (IOException e) {
            System.err.println("读取mysql.sql文件失败: " + e.getMessage());
            throw new RuntimeException("数据库初始化失败", e);
        } catch (Exception e) {
            System.err.println("执行SQL脚本时发生错误: " + e.getMessage());
            throw new RuntimeException("数据库初始化失败", e);
        }
    }
}
