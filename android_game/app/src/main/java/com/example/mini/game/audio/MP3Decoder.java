package com.example.mini.game.audio;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created by kuba on 11/23/14.
 */
public class MP3Decoder {

    public MP3Decoder()
    {

    }

    public static byte[] decode(String path)
            throws IOException, DecoderException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStream inputStream = new BufferedInputStream(fileInputStream);

        long fileLengthLong = file.length();
        int fileLength = (int)file.length();
        byte[] totalByteArray = null;

        //int byteArrayLength = byteArray.length;
        int currentArrayPosition = 0;
        try {
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();

            boolean done = false;
            while (! done) {
                outStream = new ByteArrayOutputStream();

                Header frameHeader = bitstream.readFrame();

                if (frameHeader == null) {
                    done = true;
                } else {

                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
/*
                    if (output.getSampleFrequency() != 44100
                            || output.getChannelCount() != 2) {
                        throw new IOException("NOT A MP3");
                    }*/

                    short[] pcm = output.getBuffer();
                    for (short s : pcm) {
                        outStream.write(s & 0xff);
                        outStream.write((s >> 8 ) & 0xff);
                    }

                    byte[] newByteArray = outStream.toByteArray();

                    Log.i("MP3DECODER", "Decoded " + newByteArray.length + " bytes");

                    if(totalByteArray == null) {
                        totalByteArray = Arrays.copyOf(newByteArray, newByteArray.length);
                    } else {
                        int totalLength = totalByteArray.length;
                        int newLength = newByteArray.length;

                        byte[] tmpArray = new byte[totalLength + newLength];
                        System.arraycopy(totalByteArray, 0, tmpArray, 0, totalLength);
                        System.arraycopy(newByteArray, 0, tmpArray, totalLength, newLength);

                        totalByteArray = tmpArray;
                    }
                }
                bitstream.closeFrame();
            }

        } catch (BitstreamException e) {
            throw new IOException("Bitstream error: " + e);
        } catch (DecoderException e) {
        } finally {
            //IOUtils.safeClose(inputStream);
        }
        return totalByteArray;
    }
}
