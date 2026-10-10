package in.asmj.kalahandiconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KalahandiConnectApplication {
    public static void main(String[] args) { SpringApplication.run(KalahandiConnectApplication.class, args); }
}
