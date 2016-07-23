package org.eclipse.kura.protocol.can.messages;

/**
 * Static utilities
 *
 */
public class MessageUtils {

    private MessageUtils() {
    }

    public static int buildShort(byte high, byte low) {
        return ((0xFF & (int) high) << 8) + (0xFF & (int) low);
    }
}
