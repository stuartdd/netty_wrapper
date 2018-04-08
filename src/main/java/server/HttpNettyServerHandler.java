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
 * ****************************************************************************
 *
 * Handle requests that are received via the netty server.
 *
 * Create a single HttpNettyRequest object from the netty request data.
 *
 * Call the Dispatcher with the HttpNettyRequest and a HttpNettyResponse
 *
 * Pass the HttpNettyResponse data back to the netty response data
 *
 * Most of this code is boiler plate from the Netty examples on the web.
 *
 */
package server;

import interfaces.Dispatcher;
import interfaces.Logger;
import interfaces.NettyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import java.io.IOException;

public class HttpNettyServerHandler extends SimpleChannelInboundHandler<Object> implements Loggable {

    private static final String NL = System.getProperty("line.separator");
    private final Dispatcher dispatcher;
    private final NettyConfig nettyConfig;
    private final Logger logger;

    /**
     * Create the server handler with a Dispatcher. Also pass in the NettyConfig
     * data so the handler can access it via the HttpNettyResponse object.
     *
     * @param dispatcher A fully populated Dispatcher.
     * @param nettyConfig The server configuration data.
     * @param logger A logger to be used by the handler
     */
    public HttpNettyServerHandler(Dispatcher dispatcher, NettyConfig nettyConfig, Logger logger) {
        this.dispatcher = dispatcher;
        this.nettyConfig = nettyConfig;
        this.logger = logger;
    }

    /**
     * respond to the channelReadComplete and flush the context.
     *
     * Don't ask me, I just copied the examples!
     *
     * @param ctx The context for the request
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * This is where all the action is!
     *
     * Create the HttpNettyRequest and HttpNettyResponse objects
     *
     * Pull all the data from the Netty FullHttpRequest objects and populate the
     * HttpNettyRequest.
     *
     * Call the Dispatcher when we have a complete request.
     *
     * Most of this code is boiler plate from the Netty examples on the web.
     *
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        HttpNettyRequest nRequest = new HttpNettyRequest();
        HttpNettyResponse nResponse = new HttpNettyResponse();

        if (msg instanceof FullHttpRequest) {

            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(fullHttpRequest)) {
                send100Continue(ctx);
            }

            nResponse.clear();
            nRequest.setProtocolVersion(fullHttpRequest.protocolVersion().toString());
            nRequest.setUri(fullHttpRequest.uri());
            nRequest.setMethod(fullHttpRequest.method().name());

            HttpHeaders headers = fullHttpRequest.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h : headers) {
                    CharSequence key = h.getKey();
                    CharSequence value = h.getValue();
                    nRequest.addHeader(key, value);
                }
            }

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            if (!params.isEmpty()) {
                for (Entry<String, List<String>> p : params.entrySet()) {
                    String key = p.getKey();
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                        nRequest.addParam(key, val);
                    }
                }
            }
            nRequest.setPath(queryStringDecoder.path());

            appendDecoderResult(nResponse, fullHttpRequest);

            if (msg instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) msg;
                ByteBuf content = httpContent.content();
                if (content.isReadable()) {
                    nRequest.appendToBody(content.toString(CharsetUtil.UTF_8));
                    appendDecoderResult(nResponse, fullHttpRequest);
                }
            }

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()) {
                    for (CharSequence name : trailer.trailingHeaders().names()) {
                        for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
                            nRequest.addHeader(name, value);
                        }
                    }
                }
            }
            if (nRequest.isLogging()) {
                if (nettyConfig.isLogRequestResponse()) {
                    getLogger().log("   (" + Thread.currentThread().getId() + ") " + nRequest.toStringSmall());
                }
            }

            try {
                dispatcher.handle(nRequest, nResponse, logger);
            } catch (ServerGeneralException sge) {
                getLogger().log("Handler General Error", sge);
                nResponse.setError(sge.getStatus(), sge.getMessage());
            } catch (Exception ex) {
                getLogger().log("Handler Unknown Error", ex);
                nResponse.setError(INTERNAL_SERVER_ERROR, ex.getMessage());
            }
            if (nRequest.isLogging()) {
                if (nettyConfig.isLogRequestResponse()) {
                    getLogger().log("   (" + Thread.currentThread().getId() + ") " + nResponse.toStringSmall());
                }
            }
            nResponse.setKeepAlive(HttpUtil.isKeepAlive(fullHttpRequest));
            writeResponse(fullHttpRequest, ctx, nResponse);
            if (!nResponse.isKeepAlive()) {
                // If keep-alive is off, close the connection once the content is fully written.
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void appendDecoderResult(HttpNettyResponse response, HttpObject o) {
        DecoderResult result = o.decoderResult();
        if (result.isSuccess()) {
            return;
        }
        response.append(".. WITH DECODER FAILURE: ");
        response.append(result.cause());
        response.append("\r\n");
    }

    /**
     * Use the HttpNettyResponse object to create a response.
     *
     * This handles error situations, headers and keep-alive.
     *
     * The cookie stuff is commented out as I had no use for it. Feel free to
     * experiment.
     *
     * @param currentObj A netty HttpObject
     * @param ctx A netty ChannelHandlerContext
     * @param localResponseData The response object from the handler. 
     */
    private static void writeResponse(HttpObject currentObj, ChannelHandlerContext ctx, HttpNettyResponse localResponseData) {
        // Decide whether to close the connection or not.
        // Build the response object.
        FullHttpResponse fullResponse;
        if (localResponseData.isError()) {
            fullResponse = new DefaultFullHttpResponse(
                    HTTP_1_1, currentObj.decoderResult().isSuccess() ? localResponseData.getStatus() : BAD_REQUEST,
                    Unpooled.copiedBuffer(localResponseData.toString(), CharsetUtil.UTF_8));
        } else {
            if (localResponseData.isByteData()) {
                fullResponse = new DefaultFullHttpResponse(
                        HTTP_1_1, currentObj.decoderResult().isSuccess() ? localResponseData.getStatus() : BAD_REQUEST,
                        Unpooled.copiedBuffer(localResponseData.getByteBuffer()));
            } else {
                fullResponse = new DefaultFullHttpResponse(
                        HTTP_1_1, currentObj.decoderResult().isSuccess() ? localResponseData.getStatus() : BAD_REQUEST,
                        Unpooled.copiedBuffer(localResponseData.getBuffer(), CharsetUtil.UTF_8));
            }
        }
        for (Entry<String, String> s : localResponseData.getHeaders()) {
            fullResponse.headers().set(s.getKey(), s.getValue());
        }

        if (localResponseData.isKeepAlive()) {
            // Add 'Content-Length' header only for a keep-alive connection.
            fullResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, fullResponse.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            fullResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Encode the cookie.
//        String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
//        if (cookieString != null) {
//            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
//            if (!cookies.isEmpty()) {
//                // Reset the cookies if necessary.
//                for (Cookie cookie : cookies) {
//                    response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
//                }
//            }
//        } else {
//            // Browser sent no cookie.  Add some.
//            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
//            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
//        }
        // Write the response.
        ctx.write(fullResponse);
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        if ((cause instanceof IOException) && (!cause.getMessage().contains("reset by peer"))) {
            getLogger().log(cause.getMessage());
            return;
        }
        getLogger().log("Server Handler Exception was caught", cause);
    }
    
    @Override
    public Logger getLogger() {
        return logger;
    }
}
