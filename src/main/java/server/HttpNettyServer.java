/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * ****************************************************************************
 *
 * Start and stop the Netty Server with a child handler:
 *   HttpNettyServerInitializer
 *
 * Set up the shutdown hooks so the server terminates properly
 *
 * Most of this code is boiler plate from the Netty examples on the web.
 *
 */
package server;

import interfaces.Dispatcher;
import interfaces.Logger;
import interfaces.LoggerConfig;
import interfaces.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.net.ssl.SSLException;
import server.logging.SimpleLoggerImpl;

/**
 *
 * @author stuardd
 */
public class HttpNettyServer {

    private static final String NL = System.getProperty("line.separator");
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static boolean shutDownDone = false;
    private static NettyConfig nettyConfig;
    private static Logger logger;

    public static void run(Dispatcher dispatcher, NettyConfig nettyConfig) {
        run(dispatcher, nettyConfig, null);
    }

    public static void run(Dispatcher dispatcher, NettyConfig someNettyConfig, Logger someLogger) {
        if (dispatcher == null) {
            throw new ServerSetupException("Dispatcher cannot be null");
        }

        if (someNettyConfig == null) {
            throw new ServerSetupException("NettyConfig cannot be null");
        }

        if (someNettyConfig.getPort() == 0) {
            /*
            Default value for the port in NettyConfig is 0. This MUST be set!
             */
            throw new ServerSetupException("NettyConfig port has not been set");
        }
        
        nettyConfig = someNettyConfig;
        logger = someLogger;
        /**
         * No logger provide so make one up on the spot.
         *
         * We MUST always have a logger or the code will be littered with
         *
         * 'if (logger!=null)' statements.
         */
        if (logger == null) {
            logger = new Logger() {
                String timeStamp = "yyyy_MM_dd_HH_mm_ss";

                @Override
                public void init(LoggerConfig myNettyConfig) {
                }

                @Override
                public void log(String message) {
                    System.out.println(new SimpleDateFormat(timeStamp).format(new Date()) + ": " + message);
                }

                @Override
                public void log(String message, Throwable ex) {
                    System.out.println(new SimpleDateFormat(timeStamp).format(new Date()) + ": " + message + NL + SimpleLoggerImpl.toStringException(ex));
                }
            };
        }

        if (nettyConfig.getStartupDelayMs() > 0) {
            try {
                System.err.println("Startup delay:" + nettyConfig.getStartupDelayMs());
                Thread.sleep(nettyConfig.getStartupDelayMs());
            } catch (InterruptedException ex) {
            }
        }

        // Configure SSL.
        final SslContext sslCtx;
        if (nettyConfig.isSsl()) {
            try {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (CertificateException | SSLException ex) {
                throw new ServerSetupException("Failed to set up the SSL Context", ex);
            }
        } else {
            sslCtx = null;
        }

        Runtime.getRuntime().addShutdownHook(new ShutDownThread("Shut down Hook", logger, 100));

        // Configure the server.
        bossGroup = new NioEventLoopGroup(nettyConfig.getBossEventLoopThreads());
        workerGroup = new NioEventLoopGroup(nettyConfig.getWorkerEventLoopThreads());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpNettyServerInitializer(sslCtx, dispatcher, logger, nettyConfig));

            Channel channel = b.bind(nettyConfig.getPort()).sync().channel();
            logger.log("Server started on port " + nettyConfig.getPort());
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
            }
        } catch (InterruptedException ex) {
            throw new ServerSetupException("Failed to bind the server to the port", ex);
        } finally {
            shutDown("Finally", 0);
        }
    }

    /**
     * Stop the server
     * @param desc A description for the logs. 
     * @param delay Shut down after n milliseconds.
     */
    public static void shutDown(String desc, long delay) {
        if (shutDownDone) {
            return;
        }
        getLogger().log("Shutting down in "+delay+" Milliseconds. Reason: ["+desc+"]");
        ShutDownThread shutDownByThread = new ShutDownThread(desc, getLogger(), delay);
        shutDownByThread.start();
    }

    private static class ShutDownThread extends Thread {

        private final Logger logger;
        private final String desc;
        private final long delay;

        public ShutDownThread(String desc, Logger logger, long delay) {
            this.logger = logger;
            this.desc = desc;
            this.delay = delay;
        }

        @Override
        public void run() {
            try {
                sleep(delay);
            } catch (InterruptedException ex) {

            }
            HttpNettyServer.shutDownByThread(desc, logger);
        }

    }

    private static void shutDownByThread(String desc, Logger logger) {
        if (shutDownDone) {
            return;
        }
        shutDownDone = true;
        if (logger != null) {
            logger.log("SHUT DOWN '" + desc + "' EXECUTED");
        } 
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static NettyConfig getNettyConfig() {
        return nettyConfig;
    }

    public static Logger getLogger() {
        return logger;
    }

}
