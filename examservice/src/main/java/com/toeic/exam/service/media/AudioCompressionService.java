package com.toeic.exam.service.media;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

/**
 * Service chuyên trách nén và chuẩn hóa định dạng âm thanh (MP3, 64kbps, Mono).
 */
@Service
public class AudioCompressionService {

    private static final Logger LOG = LoggerFactory.getLogger(AudioCompressionService.class);

    public File compressToMp3(File tempInput) throws Exception {
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(64000);
        audio.setChannels(1); // Mono
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audio);

        File tempOutput = File.createTempFile("audio_output_", ".mp3");
        try {
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(tempInput), tempOutput, attrs);
            return tempOutput;
        } catch (Exception e) {
            if (tempOutput.exists()) {
                tempOutput.delete();
            }
            LOG.error("Lỗi khi nén audio MP3", e);
            throw e;
        }
    }
}
