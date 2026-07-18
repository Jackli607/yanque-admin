package cn.edu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.models.users.mapper")
public class YanQueApplication {
    public static void main(String[] args) {
        SpringApplication.run(YanQueApplication.class,args);
    }
}
