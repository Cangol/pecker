package mobi.cangol.web.pecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication(scanBasePackages = {"mobi.cangol.web"})
@EnableAsync
public class PeckerApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PeckerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(PeckerApplication.class, args);
    }

    @GetMapping("/")
    public String retrieveTime() {
        return  ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy/MM/dd hh:mm:ss")).get().format(new Date());
    }



}
