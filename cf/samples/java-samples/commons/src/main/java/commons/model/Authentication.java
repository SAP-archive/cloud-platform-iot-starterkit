package commons.model;

public class Authentication {

	private String secret;

	private String pem;

	private String password;

	public Authentication(String pem, String secret) {
		this.pem = pem;
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public String getPem() {
		return pem;
	}
	
	public void setPem(String pem) {
		this.pem = pem;
	}

	public String getPassword() {
		return password;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
}
