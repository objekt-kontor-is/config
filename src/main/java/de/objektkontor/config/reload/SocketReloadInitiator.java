package de.objektkontor.config.reload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.objektkontor.config.ReloadInitiator;

public class SocketReloadInitiator extends ReloadInitiator {

    private final static Logger logger = LoggerFactory.getLogger(SocketReloadInitiator.class);

    private static final String PORT_PARAMETER = "ConfigReloadPort";
    private static final String DEFAULT_PORT = "4321";

    private static final String ADDRESS_PARAMETER = "ConfigReloadBindAddress";
    private static final String DEFAULT_ADDRESS = "localhost";

    private static final String SECRET_PARAMETER = "ConfigReloadSecret";
    private static final String DEFAULT_SECRET = "changeMe!";

    private final int port;
    private final InetAddress bindAddress;
    private final String secret;

    public SocketReloadInitiator() throws IOException {
        logger.info("Configuring Seocket Reload Initiator");
        port = getPort();
        bindAddress = getBindAddress();
        secret = getSecret();
        logger.info("Listening on: " + bindAddress + ":" + port);
        startServer();
        logger.info("Configuration reloading enabled. Use command <echo [secret] | curl telnet://localhost:" + port + "> to reload configuration");
    }

    private int getPort() {
        String value = getParameter(PORT_PARAMETER, DEFAULT_PORT);
        return Integer.parseInt(value);
    }

    private InetAddress getBindAddress() throws IOException {
        String value = getParameter(ADDRESS_PARAMETER, DEFAULT_ADDRESS);
        return InetAddress.getByName(value);
    }

    private String getSecret() {
        String value = getParameter(SECRET_PARAMETER, DEFAULT_SECRET);
        if (DEFAULT_SECRET.equals(value))
            logger.warn("UNSING DEFAULT SECRET VALUE IS NOT RECOMENDED!");
        return value;
    }

    private void startServer() throws IOException {
        @SuppressWarnings("resource")
        final ServerSocket serverSocket = new ServerSocket(port, 1, bindAddress);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true)
                    try ( Socket socket = serverSocket.accept() ) {
                        if (logger.isDebugEnabled())
                            logger.debug("Incomming connection from: " + socket.getRemoteSocketAddress());
                        socket.setTcpNoDelay(true);
                        socket.getOutputStream().write("Enter secret\n".getBytes());
                        String answer = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
                        if (secret.equals(answer)) {
                            if (logger.isDebugEnabled())
                                logger.debug("Authentification successful. Notifying handlers.");
                            notifyHandlers();
                        } else if (logger.isDebugEnabled())
                            logger.debug("Access denied");
                    } catch (IOException e) {
                        logger.warn("Exception processing incomming connection: " + e.getMessage());
                    }
            }
        };
        Thread thread = new Thread(runnable, THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }
}
