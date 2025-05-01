package com.fisa.wonq.member.controller;


import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.member.controller.dto.req.MemberRequestDTO;
import com.fisa.wonq.member.controller.dto.res.MemberResponseDTO;
import com.fisa.wonq.member.controller.dto.res.OcrResponseDTO;
import com.fisa.wonq.member.service.MemberService;
import com.fisa.wonq.member.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class MemberController {

    private final OcrService ocrService;
    private final MemberService memberService;

    /**
     * OCR 기반 사업자등록증 정보 추출
     */
    @PostMapping(
            path = "/ocr",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "사업자등록증 OCR 정보 추출",
            description = "업로드된 PDF/이미지 파일에서 사업자등록증 정보를 추출합니다.")
    public ResponseEntity<ApiResponse<OcrResponseDTO>> extractBusinessInfo(
            @Parameter(description = "사업자등록증 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        OcrResponseDTO dto = ocrService.extract(file);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dto));
    }

    @Operation(summary = "점주 회원가입",
            description = "OCR로 추출된 사업자등록번호와 기본 정보를 받아 회원가입합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MemberResponseDTO.SignupResponse>> signup(
            @RequestBody MemberRequestDTO.SignupRequest req
    ) {
        MemberResponseDTO.SignupResponse dto = memberService.signup(req);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dto));
    }

    @Operation(summary = "아이디 중복 확인",
            description = "accountId 의 사용 가능 여부를 반환합니다. \n\n true: 사용 가능한 아이디입니다. | false: 중복된 아이디이므로 사용불가합니다.")
    @GetMapping("/checkAccountId")
    public ResponseEntity<ApiResponse<Boolean>> checkAccountId(
            @RequestParam String accountId
    ) {
        boolean available = memberService.isAccountIdAvailable(accountId);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, available));
    }
}
