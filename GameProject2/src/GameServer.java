import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
	
	public static void main(String[] args) throws IOException {

		final int SERVER_PORT = 5556;
		ServerSocket server = new ServerSocket(SERVER_PORT);
		System.out.println("Waiting for clients to connect...");
		while(true) {
			Socket s = server.accept();
			System.out.println("client connected");
			
			GSServices myService = new GSServices (s);
			Thread t = new Thread(myService);
			t.start();
		}
	}
}
