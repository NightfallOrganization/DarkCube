package eu.darkcube.system.cloudban.util.ban;

public class Server {

	public static final Server GLOBAL = new Server("global");

	private String server;

	public Server(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}

	@Override
	public String toString() {
		return getServer();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Server && ((Server) obj).server.equals(server);
	}
}
