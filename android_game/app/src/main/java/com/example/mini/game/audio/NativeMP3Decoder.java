package com.example.mini.game.audio;

/**
 * This class wraps the native mp3 decoder
 * Created by Kuba on 04/12/2014.
 */
public class NativeMP3Decoder {
    /**
     * Initiates the mp3 library
     * @return
     */
    public static boolean initLib() {
        return ninitLib();
    }

    /**
     * Cleans up the mp3 library
     */
    public static void cleanupLib() {
        ncleanupLib();
    }

    /**
     * Gets error saying what happend wrong
     * @return
     *      Message indicating the error
     */
    public static String getError(int handle) {
        return ngetError(handle);
    }

    /**
     * Loads mp3
     * @return
     *      Message indicating result, see jni/mpg123/mpg123.h: mpg123_errors for more info
     */
    public static int loadMP3(String filePath, int handle) {
        return ninitMP3(filePath, handle);
    }

    /**
     * UnLoads mp3
     */
    public static void cleanupMP3(int handle) {
        ncleanupMP3(handle);
    }

    /**
     * Sets Equalizer value for given channel
     * @param channel
     *      To set equalizer value
     * @param vol
     *      Value of equalizer
     * @return
     */
    public static boolean setEQ(int channel, double vol, int handle) {
        return nsetEQ(channel, vol, handle);
    }

    /**
     * Resets equalizer
     */
    public static void resetEQ(int handle) {
        nresetEQ(handle);
    }

    /**
     * Decodes bufferLen bytes and stores in buffer.
     * Since sizeof(short) = 2 * sizeof(byte),
     * bufferLen has to be doubled.
     *
     * @param bufferLen
     *      Length of bytes to decode
     * @param buffer
     *      Buffer to store decoded data
     * @return
     *      Error message
     */
    public static int decodeMP3(int bufferLen, short[] buffer, int handle) {
        return ndecodeMP3(bufferLen, buffer, handle);
    }

    /**
     * Seeks to specified frame
     *
     * @param frames
     */
    public static void seekTo(int frames, int handle) {
        nseekTo(frames, handle);
    }

    /**
     *
     * @return
     */
    private static native boolean ninitLib();

    /**
     *
     */
    private static native void ncleanupLib();

    /**
     *
     * @return String explaining what went wrong
     */
    private static native String ngetError(int handle);

    /**
     * Initialize one MP3 file
     * @param filename
     * @return MPG123_OK
     */
    private static native int ninitMP3(String filename, int handle);

    /**
     * Cleanup all native needed resources for one MP3 file
     */
    private static native void ncleanupMP3(int handle);

    /**
     *
     * @param channel
     * @param vol
     * @return
     */
    private static native boolean nsetEQ(int channel, double vol, int handle);

    /**
     *
     */
    private static native void nresetEQ(int handle);

    /**
     * Read, decode and write PCM data to our java application
     *
     * @param bufferLen
     * @param buffer
     * @return
     */
    private static native int ndecodeMP3(int bufferLen, short[] buffer, int handle);

    /**
     *
     * @param frames
     */
    private static native void nseekTo(int frames, int handle);

    // loads the mp3 decoding library
    static { System.loadLibrary("mp3"); }
}
