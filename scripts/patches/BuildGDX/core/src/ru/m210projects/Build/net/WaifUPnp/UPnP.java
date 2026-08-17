// This file has been modified from Ken Silverman's original release
// Stubbed for browser (TeaVM) builds. Real UPnP relies on
// java.net.NetworkInterface / DatagramSocket which are not in TeaVM's
// classlib. Single-user browser sessions never enable multiplayer, so
// every method returns the "not available" answer.
package ru.m210projects.Build.net.WaifUPnp;

public class UPnP {
    public static void waitInit() {}
    public static boolean isUPnPAvailable() { return false; }
    public static boolean openPortTCP(int port) { return false; }
    public static boolean openPortUDP(int port) { return false; }
    public static boolean closePortTCP(int port) { return false; }
    public static boolean closePortUDP(int port) { return false; }
    public static boolean isMappedTCP(int port) { return false; }
    public static boolean isMappedUDP(int port) { return false; }
    public static String getExternalIP() { return null; }
    public static String getLocalIP() { return null; }
}
