package com.fisa.wonq.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisa.wonq.member.controller.dto.req.MemberRequestDTO;
import com.fisa.wonq.member.controller.dto.res.MemberResponseDTO;
import com.fisa.wonq.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        // given
        MemberRequestDTO.SignupRequest request = MemberRequestDTO.SignupRequest.builder()
                .accountId("owner123")
                .password("P@ssw0rd!")
                .email("owner@example.com")
                .phoneNo("010-1234-5678")
                .businessRegistrationNo("502-81-62379")
                .merchantName("원큐식당")
                .merchantOwnerName("홍길동")
                .merchantOwnerPhoneNo("010-9876-5432")
                .merchantEmail("contact@wonq.co.kr")
                .businessLaunchingDate("2023-01-15")
                .merchantAddress("서울특별시 강남구 테헤란로 123")
                .merchantAccount("123-456-78901234")
                .openTime("09:00")
                .closeTime("21:00")
                .build();

        MemberResponseDTO.SignupResponse response = MemberResponseDTO.SignupResponse.builder()
                .memberId(1L)
                .accountId("owner123")
                .merchantId(1L)
                .build();

        given(memberService.signup(any(MemberRequestDTO.SignupRequest.class))).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.accountId").value("owner123"))
                .andExpect(jsonPath("$.data.merchantId").value(1L));
    }

    @Test
    @DisplayName("아이디 중복 확인 성공")
    void checkAccountId_success() throws Exception {
        // given
        String accountId = "owner123";
        given(memberService.isAccountIdAvailable(accountId)).willReturn(true);

        // when, then
        mockMvc.perform(get("/api/v1/auth/checkAccountId")
                        .param("accountId", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}
