package com.evolution.network.handler;

import com.evolution.network.EnumConnectionState;
import com.evolution.network.EnumPacketDirection;
import com.evolution.network.ITickable;
import com.evolution.network.LazyLoadBase;
import com.evolution.network.NettyDecoder;
import com.evolution.network.NettyEncoder;
import com.evolution.network.NettyFrameDecoder;
import com.evolution.network.NettyFrameEncoder;
import com.evolution.network.ThreadQuickExitException;
import com.evolution.network.compression.NettyCompressionDecoder;
import com.evolution.network.compression.NettyCompressionEncoder;
import com.evomine.decode.PacketLayout;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import come.evolution.main.Main;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.ArrayUtils;

public class NettyManager extends SimpleChannelInboundHandler<PacketLayout>
{
    public static final AttributeKey<EnumConnectionState> PROTOCOL_ATTRIBUTE_KEY = AttributeKey.<EnumConnectionState>valueOf("protocol");
    private final EnumPacketDirection direction;
    private final Queue<NettyManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.<NettyManager.InboundHandlerTuplePacketListener>newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /** The active channel */
    private Channel channel;

    /** The address of the remote party */
    private SocketAddress socketAddress;

    /** The INetHandler instance responsible for processing received packets */
    private INetHandler packetListener;

    /** A String indicating why the network has shutdown. */
    private String terminationReason;
    private boolean isEncrypted;
    private boolean disconnected;

    public NettyManager(EnumPacketDirection packetDirection)
    {
        this.direction = packetDirection;
    }

    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
    {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();

        try
        {
            this.setConnectionState(EnumConnectionState.HANDSHAKING);
        }
        catch (Throwable throwable)
        {
        	throwable.printStackTrace();
        }
    }

    /**
     * Sets the new connection state and registers which packets this channel may send and receive
     */
    public void setConnectionState(EnumConnectionState newState)
    {
        this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).set(newState);
        this.channel.config().setAutoRead(true);
    }

    public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception
    {
        this.closeChannel("disconnect.endOfStream");
    }

    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception
    {
        String reason;

        if (p_exceptionCaught_2_ instanceof TimeoutException)
        {
        	reason = new String("disconnect.timeout");
        }
        else
        {
        	reason = new String("disconnect.genericReason: " + "Internal Exception: " + p_exceptionCaught_2_);
        }

        this.closeChannel(reason);
    }

    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, PacketLayout p_channelRead0_2_) throws Exception
    {
        if (this.channel.isOpen())
        {
            try
            {
                p_channelRead0_2_.processPacket(this.packetListener);
            }
            catch (ThreadQuickExitException var4)
            {
                ;
            }
        }
    }

    /**
     * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
     * connection state (protocol)
     */
    public void setNetHandler(INetHandler handler)
    {
        System.out.println("Set listener of " + this + " to " + handler);
        this.packetListener = handler;
    }

    public void sendPacket(PacketLayout packetIn)
    {
        if (this.isChannelOpen())
        {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, (GenericFutureListener[])null);
        }
        else
        {
            this.readWriteLock.writeLock().lock();

            try
            {
                this.outboundPacketsQueue.add(new NettyManager.InboundHandlerTuplePacketListener(packetIn, new GenericFutureListener[0]));
            }
            finally
            {
                this.readWriteLock.writeLock().unlock();
            }
        }
    }

    public void sendPacket(PacketLayout packetIn, GenericFutureListener <? extends Future <? super Void >> listener, GenericFutureListener <? extends Future <? super Void >> ... listeners)
    {
        if (this.isChannelOpen())
        {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, (GenericFutureListener[])ArrayUtils.add(listeners, 0, listener));
        }
        else
        {
            this.readWriteLock.writeLock().lock();

            try
            {
                this.outboundPacketsQueue.add(new NettyManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[])ArrayUtils.add(listeners, 0, listener)));
            }
            finally
            {
                this.readWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do that.
     */
    private void dispatchPacket(final PacketLayout inPacket, @Nullable final GenericFutureListener <? extends Future <? super Void >> [] futureListeners)
    {
        final EnumConnectionState enumconnectionstate = inPacket.avalibleState;
        final EnumConnectionState enumconnectionstate1 = (EnumConnectionState)this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).get();

        if (enumconnectionstate1 != enumconnectionstate)
        {

            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop())
        {
            if (enumconnectionstate != enumconnectionstate1)
            {
                this.setConnectionState(enumconnectionstate);
            }

            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);

            if (futureListeners != null)
            {
                channelfuture.addListeners(futureListeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.channel.eventLoop().execute(new Runnable()
            {
                public void run()
                {
                    if (enumconnectionstate != enumconnectionstate1)
                    {
                    	NettyManager.this.setConnectionState(enumconnectionstate);
                    }

                    ChannelFuture channelfuture1 = NettyManager.this.channel.writeAndFlush(inPacket);

                    if (futureListeners != null)
                    {
                        channelfuture1.addListeners(futureListeners);
                    }

                    channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    /**
     * Will iterate through the outboundPacketQueue and dispatch all Packets
     */
    private void flushOutboundQueue()
    {
        if (this.channel != null && this.channel.isOpen())
        {
            this.readWriteLock.readLock().lock();

            try
            {
                while (!this.outboundPacketsQueue.isEmpty())
                {
                	NettyManager.InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(networkmanager$inboundhandlertuplepacketlistener.packet, networkmanager$inboundhandlertuplepacketlistener.futureListeners);
                }
            }
            finally
            {
                this.readWriteLock.readLock().unlock();
            }
        }
    }

    /**
     * Checks timeouts and processes all packets received
     */
    public void processReceivedPackets()
    {
        this.flushOutboundQueue();

        if (this.packetListener instanceof ITickable)
        {
            ((ITickable)this.packetListener).update();
        }

        if (this.channel != null)
        {
            this.channel.flush();
        }
    }

    /**
     * Returns the socket address of the remote side. Server-only.
     */
    public SocketAddress getRemoteAddress()
    {
        return this.socketAddress;
    }

    /**
     * Closes the channel, the parameter can be used for an exit message (not certain how it gets sent)
     */
    public void closeChannel(String message)
    {
        if (this.channel.isOpen())
        {
            this.channel.close().awaitUninterruptibly();
            this.terminationReason = message;
            System.out.println("Channel closed: " + message);
        }
    }

    /**
     * True if this NetworkManager uses a memory connection (single player game). False may imply both an active TCP
     * connection or simply no active connection at all
     */
    public boolean isLocalChannel()
    {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    /**
     * Create a new NetworkManager from the server host and connect it to the server
     */
    public static NettyManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport)
    {
        final NettyManager networkmanager = new NettyManager(EnumPacketDirection.CLIENTBOUND);
        Class <? extends SocketChannel > oclass;
        LazyLoadBase <? extends EventLoopGroup > lazyloadbase;

        if (Epoll.isAvailable() && useNativeTransport)
        {
            oclass = EpollSocketChannel.class;
            lazyloadbase = new LazyLoadBase<EpollEventLoopGroup>()
            {
                protected EpollEventLoopGroup load()
                {
                    return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #0").setDaemon(true).build());
                }
            };;
        }
        else
        {
            oclass = NioSocketChannel.class;
            lazyloadbase = new LazyLoadBase<NioEventLoopGroup>()
            {
                protected NioEventLoopGroup load()
                {
                    return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #0").setDaemon(true).build());
                }
            };;
        }

        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(lazyloadbase.getValue())).handler(new ChannelInitializer<Channel>()
        {
            protected void initChannel(Channel p_initChannel_1_) throws Exception
            {
                try
                {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
                }
                catch (ChannelException var3)
                {
                    ;
                }

                p_initChannel_1_.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(30))
                .addLast("splitter", new NettyFrameDecoder())
                .addLast("decoder", new NettyDecoder())
                .addLast("prepender", new NettyFrameEncoder())
                .addLast("encoder", new NettyEncoder())
                .addLast("packet_handler", networkmanager);
            }
        })).channel(oclass)).connect(address, serverPort).syncUninterruptibly();
        return networkmanager;
    }

    public boolean isEncrypted()
    {
        return this.isEncrypted;
    }

    /**
     * Returns true if this NetworkManager has an active channel, false otherwise
     */
    public boolean isChannelOpen()
    {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean hasNoChannel()
    {
        return this.channel == null;
    }

    /**
     * Gets the current handler for processing packets
     */
    public INetHandler getNetHandler()
    {
        return this.packetListener;
    }

    /**
     * If this channel is closed, returns the exit message, null otherwise.
     */
    public String getExitMessage()
    {
        return this.terminationReason;
    }

    /**
     * Switches the channel to manual reading modus
     */
    public void disableAutoRead()
    {
        this.channel.config().setAutoRead(false);
    }

    public void setCompressionThreshold(int threshold)
    {
        if (threshold >= 0)
        {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder)
            {
                ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionThreshold(threshold);
            }
            else
            {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder)
            {
                ((NettyCompressionEncoder)this.channel.pipeline().get("compress")).setCompressionThreshold(threshold);
            }
            else
            {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
            }
        }
        else
        {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder)
            {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder)
            {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void checkDisconnected()
    {
        if (this.channel != null && !this.channel.isOpen())
        {
            if (this.disconnected)
            {
            	System.out.println("handleDisconnection() called twice");
            }
            else
            {
                this.disconnected = true;

                if (this.getExitMessage() != null)
                {
                    this.getNetHandler().onDisconnect(this.getExitMessage());
                }
                else if (this.getNetHandler() != null)
                {
                    this.getNetHandler().onDisconnect(new String("multiplayer.disconnect.generic"));
                }
            }
        }
    }

    static class InboundHandlerTuplePacketListener
    {
        private final PacketLayout packet;
        private final GenericFutureListener <? extends Future <? super Void >> [] futureListeners;

        public InboundHandlerTuplePacketListener(PacketLayout inPacket, GenericFutureListener <? extends Future <? super Void >> ... inFutureListeners)
        {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
