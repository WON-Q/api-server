package com.fisa.wonq.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fisa.wonq.member.controller.dto.OcrResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    @Value("${naver.service.url}")
    private String url;

    @Value("${naver.service.secretKey}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * MultipartFile 로 받은 사업자등록증(PDF/Image)을 Naver Clova OCR API로 보내
     * 추출된 텍스트를 OcrResponseDTO 로 반환합니다.
     */
    public OcrResponseDTO extract(MultipartFile file) {
        try {
            // 1) MultipartFile → 임시 File 로 저장
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            File temp = File.createTempFile("ocr-", "." + ext);
            file.transferTo(temp);

            // 2) OCR API 호출
            List<String> texts = callOcrApi("POST", temp.getAbsolutePath(), ext);

            // 추출된 텍스트 원본 로그
            log.info("OCR raw texts: {}", texts);

            // 3) DTO 변환 (fields 순서 고정: 번호, 상호, 대표자, 개업일, 소재지, 업종)
            OcrResponseDTO dto = OcrResponseDTO.builder()
                    .businessRegistrationNo(get(texts, 0))
                    .merchantName(get(texts, 1))
                    .representativeName(get(texts, 2))
                    .businessLaunchingDate(get(texts, 3))
                    .merchantAddress(get(texts, 4))
                    .businessType(get(texts, 5))
                    .build();

            // DTO로 매핑한 결과 로그
            log.info("OCR mapped DTO: {}", dto);

            return dto;
        } catch (Exception e) {
            log.error("OCR 처리 중 오류", e);
            throw new RuntimeException("OCR 처리에 실패했습니다.");
        }
    }

    private String get(List<String> list, int idx) {
        return (list.size() > idx) ? list.get(idx) : "";
    }

    private List<String> callOcrApi(String method, String filePath, String ext) throws IOException {
        List<String> result = new ArrayList<>();

        // 1) HttpURLConnection 세팅
        URL apiURL = new URL(url);
        HttpURLConnection con = (HttpURLConnection) apiURL.openConnection();
        con.setUseCaches(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setReadTimeout(30000);
        con.setRequestMethod(method);

        String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", "");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.setRequestProperty("X-OCR-SECRET", secretKey);

        // 2) JSON part 생성
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("version", "V2");
        messageJson.put("requestId", UUID.randomUUID().toString());
        messageJson.put("timestamp", System.currentTimeMillis());
        ObjectNode image = objectMapper.createObjectNode();
        image.put("format", ext);
        image.put("name", "image");
        ArrayNode images = objectMapper.createArrayNode().add(image);
        messageJson.set("images", images);

        // 3) 요청 전송
        con.connect();
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            writeMultiPart(wr, messageJson.toString(), new File(filePath), boundary);
        }

        // 4) 응답 수신
        int responseCode = con.getResponseCode();
        InputStream respStream = (responseCode == 200)
                ? con.getInputStream()
                : con.getErrorStream();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(respStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }

        // 5) JSON 파싱
        JsonNode root = objectMapper.readTree(sb.toString());
        JsonNode fields = root.path("images").get(0).path("fields");
        if (fields.isArray()) {
            for (JsonNode f : fields) {
                result.add(f.path("inferText").asText());
            }
        }
        return result;
    }

    private void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
        // (1) JSON 파트
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"message\"\r\n\r\n");
        writer.append(jsonMessage).append("\r\n").flush();

        // (2) 파일 파트
        if (file.isFile()) {
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n").flush();

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = fis.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                out.write("\r\n".getBytes());
            }
            writer.append("--").append(boundary).append("--\r\n").flush();
        }
    }
}
