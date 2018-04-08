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
 * Initialise the server and introduce HttpNettyServerHandler.
 *
 * Most of this code is boiler plate from the Netty examples on the web.
 *
 */
package server;

import interfaces.Dispatcher;
import interfaces.Logger;
import interfaces.NettyConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

/**
 *
 * @author stuar
 */
public class HttpNettyServerInitializer extends ChannelInitializer<SocketChannel> implements Loggable {

    private final SslContext sslCtx;
    private final Dispatcher dispatcher;
    private final Logger logger;
    private final NettyConfig nettyConfig;

    public HttpNettyServerInitializer(SslContext sslCtx, Dispatcher dispatcher, Logger logger, NettyConfig nettyConfig) {
        this.sslCtx = sslCtx;
        this.dispatcher = dispatcher;
        this.logger = logger;
        this.nettyConfig = nettyConfig;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        p.addLast(new HttpNettyServerHandler(dispatcher, nettyConfig, getLogger()));
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
