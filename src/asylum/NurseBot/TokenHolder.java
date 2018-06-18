package asylum.NurseBot;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TokenHolder {
	private static TokenHolder holder;
	
	public static TokenHolder getInstance() throws IOException {
		if (holder == null)
			holder = new TokenHolder();
		
		return holder;
	}
	
	private static final String FILE = "token.priv";
	
	private String token;

	public String getToken() {
		return token;
	}

	private TokenHolder() throws IOException {
		token = new String(Files.readAllBytes(Paths.get(FILE)), Charset.forName("UTF-8")).trim();
	}
}
