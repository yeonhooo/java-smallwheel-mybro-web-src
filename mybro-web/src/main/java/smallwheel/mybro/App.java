package smallwheel.mybro;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class App extends SpringBootServletInitializer {
	
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx = SpringApplication.run(App.class, args);
		
		String port = StringUtils.defaultIfEmpty( ctx.getEnvironment().getProperty("server.port"), "8080");
		String url = String.format("http://%s%s", InetAddress.getLocalHost().getHostAddress(), ( port != null ? ":" + port : "" ) );

		if ( SystemUtils.IS_OS_WINDOWS ) {
			Runtime.getRuntime().exec(String.format("rundll32 url.dll,FileProtocolHandler %s",  url));
		} else if ( SystemUtils.IS_OS_MAC ) {
			Runtime.getRuntime().exec(String.format("open %s",  url));
		}
	}
}