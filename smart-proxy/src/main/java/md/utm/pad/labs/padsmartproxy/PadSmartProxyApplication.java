package md.utm.pad.labs.padsmartproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
@ComponentScan(basePackages = { "md.utm.pad.labs" })
public class PadSmartProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PadSmartProxyApplication.class, args);
	}

	@Bean(destroyMethod = "stop")
    public RedisServer startRedis() {
        try {
            RedisServer redisServer = new RedisServer(6379);
            redisServer.start();
            return redisServer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	@Bean
	public URI firstDataWarehouse() {
        try {
            return new URI("http://localhost:8080");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public URI secondDataWarehouse() {
        try {
            return new URI("http://localhost:8081");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
