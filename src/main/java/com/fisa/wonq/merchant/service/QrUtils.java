package com.fisa.wonq.merchant.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class QrUtils {

    private final S3UploadService s3UploadService;
    private final QrCodeService qrCodeService;

    /**
     * targetUrl을 QR 코드(PNG)로 생성하여 S3에 업로드, DB에 저장 후 URL 반환
     */
    public String generateQrCodeAndUpload(Long memberId, String targetUrl) {
        try {
            // QR 코드 생성
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(targetUrl, BarcodeFormat.QR_CODE, 300, 300);

            // PNG 바이트로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            byte[] pngData = baos.toByteArray();

            // S3에 업로드
            String imageUrl = s3UploadService.upload(pngData, "qr-code.png", "image/png");

            // DB에 QR 기록 저장
            qrCodeService.saveQrCode(memberId, targetUrl, imageUrl);

            return imageUrl;
        } catch (WriterException | IOException e) {
            throw new ValidationException("QR 코드 생성 또는 업로드 실패: " + e.getMessage(), e);
        }
    }
}
