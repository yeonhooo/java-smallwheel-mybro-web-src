package smallwheel.mybro;

import java.io.IOException;
import java.net.InetAddress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class App extends SpringBootServletInitializer {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);

		if (System.getProperty("os.name").startsWith("Windows")) {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://" + InetAddress.getLocalHost().getHostAddress());
		}
	}

}