package generalplus.com.GPLib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

public class ComAir5Wrapper {
	private static AudioRecord audioRecord = null;
	private static AudioTrack audioTarck = null;
	private static String DBGTag = "GPComAir5Wrapper";
	public int i32SampleRate = 48000;
	private static int i32RecorderResourceType = eReocrderResourceType.eReocrderResourceType_Default.getVal();
	private static boolean bFindRecorderResourceType = false, bTestComAirStatus = false, bRecording = false;
	private ArrayList<byte[]> listCommandData = new ArrayList<byte[]>();
	
	public enum eReocrderResourceType
	{	
		eReocrderResourceType_Default(0x00),
		eReocrderResourceType_MIC(0x01),
		eReocrderResourceType_VOICE_RECOGNITION(0x06),
		eReocrderResourceType_CAMCORDER(0x05),		
		eReocrderResourceType_VOICE_UPLINK(0x02),
		eReocrderResourceType_VOICE_DOWNLINK(0x03),
		eReocrderResourceType_VOICE_CALL(0x04);	
		private final int val;
		private eReocrderResourceType(int v) { val = v; }
		public int getVal() { return val; }
	};
	
	public enum eComAir5Status
	{
		eComAir5Status_DecodeCommand(0xFFF0),
		eComAir5Status_CalibrationTypeFailed(0xFFF1),
		eComAir5Status_CalibrationProcessFailed(0xFFF2),
		eComAir5Status_CalibrationProcessSuccess(0xFFF3),
		eComAir5Status_RecorderInitializationFailed(0xFFF4);
		private final int val;
		private eComAir5Status(int v) { val = v; }
		public int getVal() { return val; }
	};
	
	public enum eComAirErrCode
	{
		COMAIR5_NOERR(0),
		COMAIR5_AUDIONOTINIT(1),
		COMAIR5_AUIDOUINTFAILED(2),
		COMAIR5_ENABLEIORECFAILED(3),
		COMAIR5_PROPERTYNOTFOUND(4),
		COMAIR5_PROPERTYOPERATIONFAILED(5),
		COMAIR5_PROPERTYSIZEMISSMATCH(6),
		COMAIR5_PLAYCOMAIR5SOUNDFAILED(7);
		private final int val;
		private eComAirErrCode(int v) { val = v; }
	};
	
	public enum eComAir5PropertyTarget
	{
		eComAir5PropertyTarget_Encode(0),
		eComAir5PropertyTarget_Decode(1);
		private final int val;
		private eComAir5PropertyTarget(int v) { val = v; }
	};
	
	public enum eComAir5Property
	{
		eComAir5Property_RegCode(0),		//Encode/Decode,Set only,String
		eComAir5Property_CentralFreq(1),	//Encode/Decode,Set/Get,Int
		eComAir5Property_iDfValue(2),		//Encode/Decode,Set/Get,Int
		eComAir5Property_SampleRate(3),		//Encode/Decode,Set,Function Pointer
		eComAir5Property_SaveRawData(4),	//Decode,Set,Function Pointer
		eComAir5Property_Threshold(5);		//Decode,Set/Get,Int
		private final int val;
		private eComAir5Property(int v) { val = v; }
	};
	
	public class ComAir5Command 
	{
		private int		i32command;
		private float	f32Delay;
		public ComAir5Command(int iCmd, float fDelay){
			i32command = iCmd;
			f32Delay = fDelay;
		}
	}

	// Native Library Start -------
	static {
		try {
			Log.i(DBGTag, "Trying to load GPComAir5Lib.so ...");
			System.loadLibrary("GPComAir5Lib");
		} catch (UnsatisfiedLinkError Ule) {
			Log.e(DBGTag, "Cannot load GPComAir5Lib.so ...");
			Ule.printStackTrace();
		} finally {
		}
	}
	private static native int InitComAir5();
	private static native int UnitComAir5();
	private static native int StartComAir5Decode();
	private static native int StopComAir5Decode();
	private static native int ComAir5Decode(short[] shBuffer);
	public native int SetComAir5Property(int eTarget, int eProperty, Object ObjectValue);
	public native int GetComAir5Property(int eTarget, int eProperty);
	private static native byte[] PlayComAir5Cmd(int i32Command, float SoundValume);
	private static native byte[] PlayComAir5CmdList(int i32CommandCount, ComAir5Command[] pCommandList, float SoundValume);
	// Native Library End -------
	
	//===============================================================================
	// Constructor & Listener
	//===============================================================================
	public void SetDisplayCommandValueHelper(DisplayCommandValueHelper helper) 
	{
		  ComAir5Wrapper.displayCmdHelper = helper;
	}
	private static DisplayCommandValueHelper displayCmdHelper;

	public static abstract class DisplayCommandValueHelper
	{
		public abstract void getCommand(int count, int cmdValue, int statusValue);
	}
	
	//===============================================================================
	// CALLBACK Function MUST Exist, DO NOT ALTER API
	//===============================================================================
	int decodedCmdValue = -1;
	private static int m_i32CommnadCnt = 0;
	public void CommandDecoded(int cmdValue)
	{
		decodedCmdValue = cmdValue;
		if(bTestComAirStatus && eComAir5Status.eComAir5Status_RecorderInitializationFailed.getVal() != decodedCmdValue && eComAir5Status.eComAir5Status_CalibrationTypeFailed.getVal() != decodedCmdValue)
		{
			bFindRecorderResourceType = true;
			bTestComAirStatus = false;
			return;
		}
		
		if (displayCmdHelper != null)
		{
			if(decodedCmdValue == eComAir5Status.eComAir5Status_RecorderInitializationFailed.getVal())
				displayCmdHelper.getCommand(0, decodedCmdValue, eComAir5Status.eComAir5Status_RecorderInitializationFailed.getVal());
			else if(decodedCmdValue == eComAir5Status.eComAir5Status_CalibrationTypeFailed.getVal())
				displayCmdHelper.getCommand(0, decodedCmdValue, eComAir5Status.eComAir5Status_CalibrationTypeFailed.getVal());
			else
			{
				displayCmdHelper.getCommand(m_i32CommnadCnt, decodedCmdValue, eComAir5Status.eComAir5Status_DecodeCommand.getVal());
				m_i32CommnadCnt++;
			}
		}
	}
		
	public int StartComAir5DecodeProcess()
	{
		SetComAir5Property(eComAir5PropertyTarget.eComAir5PropertyTarget_Decode.ordinal(), eComAir5Property.eComAir5Property_SampleRate.ordinal(), i32SampleRate);
		SetComAir5Property(eComAir5PropertyTarget.eComAir5PropertyTarget_Encode.ordinal(), eComAir5Property.eComAir5Property_SampleRate.ordinal(), i32SampleRate);
		int iResult = InitComAir5();
		iResult = StartComAir5Decode();
		if(ReceiveCommandThread == null)
		{			
			ReceiveCommandThread = new Thread(new ReceiveCommandRunnable());
			ReceiveCommandThread.start();
		}
		return iResult;
	}
	
	public int StopComAir5DecodeProcess()
	{
		if(ReceiveCommandThread != null)
			bRecording = false;
		int iResult = StopComAir5Decode();
		iResult = UnitComAir5();		
		m_i32CommnadCnt = 0;
		return iResult;
	}	
	
	private static Thread ReceiveCommandThread = null;	
	class ReceiveCommandRunnable implements Runnable{
		
		private int sampleRateInHz = i32SampleRate;
		private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
		private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		private int bufferSize, bufferRead, i32DecodeCommand;
		private short[] buffer;
		
		ReceiveCommandRunnable()
		{
			bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		    buffer = new short[bufferSize];



		    audioRecord = new AudioRecord(i32RecorderResourceType, sampleRateInHz, channelConfig, audioFormat, bufferSize*10);
		    if(audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
		    	bRecording = true;
		    else
		    {
		    	bRecording = false;
		    	CommandDecoded(eComAir5Status.eComAir5Status_RecorderInitializationFailed.getVal());
		    }
		}
		
		@Override
		public void run() {
			if(audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
				audioRecord.startRecording();
			while(bRecording)
			{
				bufferRead = audioRecord.read(buffer, 0, bufferSize);
				if(bufferRead > 0)
				{
					short[] tmpBuffer = new short[bufferRead];
					System.arraycopy(buffer, 0, tmpBuffer, 0, bufferRead);
					i32DecodeCommand = ComAir5Decode(tmpBuffer);
					tmpBuffer = null;
				}
			}
			if(audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
			{
				audioRecord.stop();
				audioRecord.release();
			}
			audioRecord = null;
			ReceiveCommandThread = null;
		}
	}
	
	public void PlayComAirCmd(int iCmd, float fVolume) {	
		
		byte[] byaryPCM = PlayComAir5Cmd(iCmd, fVolume);
		if(byaryPCM.length == 1)
			return;
		
		if(audioTarck != null)
			audioTarck.stop();
		synchronized(listCommandData)
		{
			listCommandData.clear();
			listCommandData.add(byaryPCM);
		}
		byaryPCM = null;
		
		if(PlayCommandThread == null)
		{
			PlayCommandThread = new Thread(new PlayCommandRunnable(fVolume));
			PlayCommandThread.start();
		}		
	}
	
	public void PlayComAirCmdList(ComAir5Command[] cmdList, float fVolume)
	{
		byte[] byaryPCM = PlayComAir5CmdList(cmdList.length, cmdList, fVolume);
		if(byaryPCM.length == 1)
			return;

		if(audioTarck != null)
			audioTarck.stop();
		synchronized(listCommandData)
		{
			listCommandData.clear();
			listCommandData.add(byaryPCM);
		}
		byaryPCM = null;
		
		if(PlayCommandThread == null)
		{
			PlayCommandThread = new Thread(new PlayCommandRunnable(fVolume));
			PlayCommandThread.start();
		}
	}
	
	public boolean IsComAir5CmdPlaying()
	{
		if(PlayCommandThread == null || listCommandData == null)
			return false;
		
		if(listCommandData.size() == 0)
			return false;
		else
			return true;
	}
	
	private static Thread PlayCommandThread = null;	
	class PlayCommandRunnable implements Runnable{
		private int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
		private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		private int minbufferSize;
		private float i16VoluemValue;
		private byte cmddata[];
		
		PlayCommandRunnable(float VolumeValue)
		{
			i16VoluemValue = VolumeValue;			
			minbufferSize = AudioTrack.getMinBufferSize(i32SampleRate, channelConfiguration, audioEncoding);
			audioTarck = new AudioTrack(AudioManager.STREAM_MUSIC, i32SampleRate, channelConfiguration, audioEncoding, minbufferSize, AudioTrack.MODE_STREAM);
			audioTarck.setStereoVolume(i16VoluemValue, i16VoluemValue);
			audioTarck.play();
		}
		
		@Override
		public void run() {

			while(!listCommandData.isEmpty())
			{
				audioTarck.play();
				cmddata = listCommandData.get(0);	
				listCommandData.remove(0);
				audioTarck.write(cmddata, 0, cmddata.length);					
				cmddata = null;
			}
			audioTarck = null;
			PlayCommandThread = null;
		}
	}	
		
	public void CalibrateComAir5Recorder()
	{
		InitComAir5();
		StartComAir5Decode();		
		if(CalibrateComAir5RecorderThread == null)
		{
			CalibrateComAir5RecorderThread = new Thread(new CalibrateComAir5RecorderRunnable());
			CalibrateComAir5RecorderThread.start();
		}
	}
	
	public int GetComAir5RecorderType()
	{
		return i32RecorderResourceType;
	}
	
	static Thread CalibrateComAir5RecorderThread = null;
	class CalibrateComAir5RecorderRunnable implements Runnable
	{
		private int i32TestIndex = 0;
		private int i32TestCommand = 0x0F;
		@Override
		public void run() {
			
			i32TestIndex = 0;
			bTestComAirStatus = true;
			bFindRecorderResourceType = false;
			while(bTestComAirStatus && i32TestIndex < eReocrderResourceType.values().length)
			{				
				try {
					if(i32TestIndex != 0)
						CommandDecoded(eComAir5Status.eComAir5Status_CalibrationTypeFailed.getVal());
					i32RecorderResourceType = eReocrderResourceType.values()[i32TestIndex].getVal();
					
					while(ReceiveCommandThread != null)
					{
						Thread.sleep(10);
					}
					if(ReceiveCommandThread == null)
					{
						ReceiveCommandThread = new Thread(new ReceiveCommandRunnable());
						ReceiveCommandThread.start();						
					}
					while(audioRecord == null && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING)
					{
						Thread.sleep(10);
					}
					Thread.sleep(10);
					if(audioRecord != null)
					{					
						PlayComAirCmd(i32TestCommand, 1.0f);
						while(audioTarck != null)
						{	
							Thread.sleep(100);
						}
						Thread.sleep(500);
					}
					if(ReceiveCommandThread != null)
					{
						bRecording = false;
					}				
					i32TestIndex++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
			if (displayCmdHelper != null)
			{
				if(bFindRecorderResourceType)
					displayCmdHelper.getCommand(0, i32TestCommand, eComAir5Status.eComAir5Status_CalibrationProcessSuccess.getVal());
				else
					displayCmdHelper.getCommand(0, i32TestCommand, eComAir5Status.eComAir5Status_CalibrationProcessFailed.getVal());
			}
			StopComAir5DecodeProcess();
			CalibrateComAir5RecorderThread = null;			
		}
		
	}
}
