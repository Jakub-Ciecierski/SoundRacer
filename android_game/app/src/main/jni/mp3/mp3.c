/*
arm:
sizeof(char)      = 1
sizeof(short)     = 2
sizeof(int)       = 4
sizeof(long)      = 4
sizeof(long long) = 8
sizeof(float)     = 4
sizeof(double)    = 8
*/

#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <arpa/inet.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "mp3.h"
#include "../mpg123/mpg123.h"


mpg123_handle *mh = NULL;
mpg123_handle *mh_analysis = NULL;


/*
 * Init MPG123, must be done one per process and before all other functions!
 */
JNIEXPORT jboolean JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_ninitLib(JNIEnv* env, jobject this)
{	 
	jint err = MPG123_ERR;

	if(mpg123_init() != MPG123_OK || (mh = mpg123_new(NULL, &err)) == NULL || (mh_analysis = mpg123_new(NULL, &err)) == NULL)
		return JNI_FALSE;

	/*
	 * Setup needed format options
	 */
	mpg123_format_none(mh);
	if ((err = mpg123_format(mh, 44100, MPG123_STEREO, MPG123_ENC_SIGNED_16)) != MPG123_OK
	||(err = mpg123_format(mh_analysis, 44100, MPG123_STEREO, MPG123_ENC_SIGNED_16)) != MPG123_OK)
		return JNI_FALSE;

	mpg123_volume();
	return JNI_TRUE;
}

/*
 * Finish our MPG123 library
 */
JNIEXPORT void JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_ncleanupLib(JNIEnv* env, jobject this)
{
	mpg123_delete(mh);
	mpg123_delete(mh_analysis);
	mpg123_exit();
	mh = NULL;
	mh_analysis = NULL;
}

/*
 * Get last error, explaining string
 */
JNIEXPORT jstring JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_ngetError(JNIEnv* env, jobject this, jint handle)
{
    const char *err_string = NULL;
    if(handle = 0)
    {
        err_string = mpg123_strerror(mh);
    }
    if(handle = 1)
    {
        err_string = mpg123_strerror(mh_analysis);
    }
	return (*env)->NewStringUTF(env, err_string);
}

/*
 * Init one MP3 file
 */
JNIEXPORT jint JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_ninitMP3(JNIEnv* env, jobject this, jstring filename, jint handle)
{
	jint err = MPG123_ERR;

	const char *mfile = (*env)->GetStringUTFChars(env, filename, NULL); // This is a UTF8 String!
	if(mfile == NULL)
		return -2;

	// Init and access new MP3 file
	if(handle == 0)
	{
	    if((err = mpg123_open(mh, mfile)) != MPG123_OK)
		    return err;
	}
    if(handle == 1)
    {
        if((err = mpg123_open(mh_analysis, mfile)) != MPG123_OK)
            return err;
    }
  	(*env)->ReleaseStringUTFChars(env, filename, mfile);


/* 
	int i = 1;
 
	double in = 1.0;

	if(mpg123_eq(mh, MPG123_LEFT | MPG123_RIGHT, 0, in) != MPG123_OK)
	 	dprintf(0, "eq failed\n");

	in = 1.0;
	
  for(i = 1; i < 32; i++)
  {
	  if(mpg123_eq(mh, MPG123_LEFT | MPG123_RIGHT, i, in) != MPG123_OK)
		  dprintf(0, "eq failed\n");
  }
*/

	err = MPG123_OK;
	return err;
}

/*
 * Close and finish all handles to one MP3 file
 */
void Java_com_example_mini_game_audio_NativeMP3Decoder_ncleanupMP3(JNIEnv* env, jobject this, jint handle)
{
    if(handle == 0)
	    mpg123_close(mh);
    if(handle == 1)
        mpg123_close(mh_analysis);
}

/*
 *
 */
JNIEXPORT jboolean JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_nsetEQ(JNIEnv* env, jobject this, jint ch, jdouble val, jint handle)
{
    if(handle == 0)
    {
	    if(mpg123_eq(mh, MPG123_LEFT | MPG123_RIGHT, ch, val) != MPG123_OK)
		    return JNI_FALSE;
    }
    if(handle == 1)
    {
        if(mpg123_eq(mh_analysis, MPG123_LEFT | MPG123_RIGHT, ch, val) != MPG123_OK)
            return JNI_FALSE;
    }

	return JNI_TRUE;
}

/*
 *
 */
JNIEXPORT void JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_nresetEQ(JNIEnv* env, jobject this, jint handle)
{
    if(handle == 0)
	    mpg123_reset_eq(mh);
	if(handle == 1)
        mpg123_reset_eq(mh_analysis);
}

/*
 * Decode our MP3 file
 */
JNIEXPORT jint JNICALL  Java_com_example_mini_game_audio_NativeMP3Decoder_ndecodeMP3(JNIEnv* env, jobject this, jint inlen, jshortArray jpcm, jint handle)
{
	jint err = MPG123_ERR;
	jint outlen = 0;
	jshort *pcm = NULL;

	// Write PCM Data to byte array
	pcm = (*env)->GetShortArrayElements(env, jpcm, NULL);
	if( handle == 0 )
	{
	    err = mpg123_read(mh, (unsigned char*) pcm, inlen, &outlen);
	}
	if( handle == 1 )
    {
        err = mpg123_read(mh_analysis, (unsigned char*) pcm, inlen, &outlen);
    }
	(*env)->ReleaseShortArrayElements(env, jpcm, pcm, 0);

	return err;
}

/*
 * Seek to specified offset, starting from 0, in milliseconds
 */
JNIEXPORT void JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_nseekTo(JNIEnv* env, jobject this, jint pos, jint handle)
{
    if( handle == 0)
    {
	    if(mh != NULL)
		    mpg123_seek(mh, pos, SEEK_SET);
    }
    if( handle == 1)
    {
        if(mh_analysis != NULL)
            mpg123_seek(mh_analysis, pos, SEEK_SET);
    }
}

JNIEXPORT jint JNICALL Java_com_example_mini_game_audio_NativeMP3Decoder_nTell(JNIEnv* env, jobject this, jint handle)
{
    off_t pos = 0;
    if(handle == 0)
        pos = mpg123_tell(mh);
    if(handle == 1)
        pos = mpg123_tell(mh_analysis);
    return pos;
}
