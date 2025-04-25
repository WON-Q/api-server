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
import java.nio.charset.StandardCharsets;
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

    public OcrResponseDTO extract(MultipartFile file) {
        try {
            String ext  = FilenameUtils.getExtension(file.getOriginalFilename());
            File   temp = File.createTempFile("ocr-", "." + ext);
            file.transferTo(temp);

            List<String> texts = callOcrApi("POST", temp.getAbsolutePath(), ext);
            log.info("OCR raw texts: {}", texts);

            String regNo    = findValueAfter(texts, "등록번호");

            OcrResponseDTO dto = OcrResponseDTO.builder()
                    .businessRegistrationNo(regNo)
//                    .merchantName(merchant)
//                    .representativeName(owner)
//                    .businessLaunchingDate(openDate)
//                    .merchantAddress(address)
                    .build();

            log.info("OCR mapped DTO: {}", dto);
            return dto;

        } catch (Exception e) {
            log.error("OCR 처리 중 오류", e);
            throw new RuntimeException("OCR 처리에 실패했습니다.");
        }
    }

    /**
     * "key:" 혹은 "key" 텍스트가 있는 인덱스를 찾고,
     * 바로 다음이 ":" 이면 그 다음 값을, 아니면 바로 다음 값을 반환
     */
    private String findValueAfter(List<String> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(key)) {
                int next = i + 1;
                if (next < list.size() && ":".equals(list.get(next))) {
                    next++;
                }
                return (next < list.size()) ? list.get(next) : "";
            }
        }
        return "";
    }

    /**
     * "개업연월일:" 텍스트를 찾고,
     * 뒤따르는 [":"], year, "년", month, "월", day, "일" 패턴에서
     * 숫자만 추출해 yyyy-MM-dd 로 포맷
     */
    private String parseDate(List<String> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(key)) {
                int idx = i + 1;
                // 콜론 건너뛰기
                if (idx < list.size() && ":".equals(list.get(idx))) {
                    idx++;
                }
                // 이제 idx: year, idx+1: "년", idx+2: month, idx+3: "월", idx+4: day, idx+5: "일"
                if (idx + 5 < list.size()) {
                    String year  = list.get(idx).replaceAll("\\D", "");
                    String month = list.get(idx + 2).replaceAll("\\D", "");
                    String day   = list.get(idx + 4).replaceAll("\\D", "");
                    if (!year.isEmpty() && !month.isEmpty() && !day.isEmpty()) {
                        return String.format("%s-%02d-%02d",
                                year,
                                Integer.parseInt(month),
                                Integer.parseInt(day));
                    }
                }
            }
        }
        return "";
    }

    private List<String> callOcrApi(String method, String filePath, String ext) throws IOException {
        List<String> result = new ArrayList<>();

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

        // JSON part
        ObjectNode messageJson = objectMapper.createObjectNode()
                .put("version", "V2")
                .put("requestId", UUID.randomUUID().toString())
                .put("timestamp", System.currentTimeMillis());
        ObjectNode image = objectMapper.createObjectNode()
                .put("format", ext)
                .put("name", "image");
        messageJson.set("images", objectMapper.createArrayNode().add(image));

        con.connect();
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            writeMultiPart(wr, messageJson.toString(), new File(filePath), boundary);
        }

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

        JsonNode fields = objectMapper.readTree(sb.toString())
                .path("images").get(0).path("fields");
        if (fields.isArray()) {
            for (JsonNode f : fields) {
                result.add(f.path("inferText").asText());
            }
        }
        return result;
    }

    private void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        // JSON 파트
        writer.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"message\"\r\n\r\n")
                .append(jsonMessage).append("\r\n").flush();

        // 파일 파트
        if (file.isFile()) {
            writer.append("--").append(boundary).append("\r\n")
                    .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"\r\n")
                    .append("Content-Type: application/octet-stream\r\n\r\n")
                    .flush();

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buf = new byte[8192];
                int    n;
                while ((n = fis.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                out.write("\r\n".getBytes());
            }
            writer.append("--").append(boundary).append("--\r\n").flush();
        }
    }
}

