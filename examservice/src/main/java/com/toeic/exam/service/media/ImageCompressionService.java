package com.toeic.exam.service.media;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service chuyên trách tối ưu hóa và chuyển đổi hình ảnh sang chuẩn WebP.
 */
@Service
public class ImageCompressionService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageCompressionService.class);
    private static final int MAX_WIDTH = 1200;
    private static final int WEBP_QUALITY = 75;

    public byte[] compressToWebp(InputStream inputStream) throws Exception {
        try {
            ImmutableImage image = ImmutableImage.loader().fromStream(inputStream);
            if (image.awt().getWidth() > MAX_WIDTH) {
                image = image.scaleToWidth(MAX_WIDTH);
            }
            return image.bytes(WebpWriter.DEFAULT.withQ(WEBP_QUALITY));
        } catch (Exception e) {
            LOG.error("Lỗi khi tối ưu hình ảnh WebP", e);
            throw e;
        }
    }
}
