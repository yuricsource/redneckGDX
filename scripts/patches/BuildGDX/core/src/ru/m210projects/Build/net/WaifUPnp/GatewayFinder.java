// This file has been modified from Ken Silverman's original release
// Stubbed for browser (TeaVM) builds: multiplayer NAT traversal requires
// java.net.{NetworkInterface,DatagramSocket,DatagramPacket,InetAddress,
// InetSocketAddress,Inet4Address} which are not in TeaVM's classlib.
// Single-user browser sessions never call this; return immediately.
package ru.m210projects.Build.net.WaifUPnp;

abstract class GatewayFinder {
    public GatewayFinder() {
        // no-op; multiplayer disabled in browser build
    }

    public abstract void gatewayFound(Gateway g);
}
