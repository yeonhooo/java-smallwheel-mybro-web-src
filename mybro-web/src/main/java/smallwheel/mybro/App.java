package smallwheel.mybro;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

@EnableAutoConfiguration
@ComponentScan
public class App extends SpringBootServletInitializer {
	
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx = SpringApplication.run(App.class, args);
		
		String ip = InetAddress.getLocalHost().getHostAddress();
		String port = StringUtils.defaultIfEmpty( ctx.getEnvironment().getProperty("server.port"), "8080" );
		String url = "http://" + String.join( ":", Arrays.asList( ip, port ) );
		
		if ( SystemUtils.IS_OS_WINDOWS ) {
			Runtime.getRuntime().exec(String.format("rundll32 url.dll,FileProtocolHandler %s",  url));
		} else if ( SystemUtils.IS_OS_MAC ) {
			Runtime.getRuntime().exec(String.format("open %s",  url));
		} else {
			System.out.println( String.format("OS version : %s %s", SystemUtils.OS_NAME, SystemUtils.OS_VERSION) );
		}
	}
}