/**
 * Copyright 2016 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.tau.linea.network;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.lmax.disruptor.EventHandler;
import com.srotya.tau.nucleus.utils.NetworkUtils;
import com.srotya.tau.wraith.TauEvent;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class InternalUDPTransportClient implements EventHandler<TauEvent> {

	private static final Logger logger = Logger.getLogger(InternalUDPTransportClient.class.getName());
	public static final short CLIENT_PORT = 9998;
	private ByteBuffer buf = ByteBuffer.allocate(1024);
	private Channel channel;
	private short bufferEventCount;

	public void init() throws Exception {
		buf.putShort(bufferEventCount);
		
		NetworkInterface iface = NetworkUtils.selectDefaultIPAddress(true);
		Inet4Address address = NetworkUtils.getIPv4Address(iface);
		logger.info("Selected default interface:"+iface.getName()+"\twith address:"+address.getHostAddress());
		
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		Bootstrap b = new Bootstrap();
		channel = b.group(workerGroup).channel(NioDatagramChannel.class).handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline();
			}
		}).bind(address, CLIENT_PORT).sync().channel();

		TauEvent event = new TauEvent();
		event.getHeaders().put("host", "xyz.srotya.com");
		event.getHeaders().put("message",
				"ix-dc9-19.ix.netcom.com - - [04/Sep/1995:00:00:28 -0400] \"GET		 /html/cgi.html HTTP/1.0\" 200 2217\r\n");
		event.getHeaders().put("value", 10);
		event.setEventId(13123134234L);
		
		for (int i = 0; i < 100; i++) {
			onEvent(event, 0, true);
		}

		channel.closeFuture().await(3000);
		workerGroup.shutdownGracefully().sync();
	}

	public static void main(String[] args) throws Exception {
		InternalUDPTransportClient client = new InternalUDPTransportClient();
		client.init();
	}

	@Override
	public void onEvent(TauEvent event, long sequence, boolean endOfBatch) throws Exception {
		byte[] bytes = InternalTCPTransportServer.KryoObjectEncoder.eventToByteArray(event,
				InternalTCPTransportServer.COMPRESS);
		if (bytes.length > 1024) {
			// discard
			System.err.println("Discarded event");
		}
		if (buf.remaining() - bytes.length >= 0) {
			buf.put(bytes);
			bufferEventCount++;
		} else {
			buf.putShort(0, bufferEventCount);
			buf.rewind();
			System.out.println("Writing messages:"+bufferEventCount);
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer(buf), new InetSocketAddress("localhost", InternalUDPTransportServer.SERVER_PORT)));
			bufferEventCount = 0;
			buf.rewind();
			buf.putShort(bufferEventCount);
			buf.put(bytes);
		}
	}

}
