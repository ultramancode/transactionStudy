package com.example.transactiontest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    @Test
    void outerTxOff_success() {

        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //then: 모든 데이터가 정상 저장
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());

    }


    /**
     * memberService    @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    //LogRepository에서 RuntimeException 터뜨리기, 멤버는 저장 되고 로그는 롤백
    @Test
    void outerTxOff_fail() {

        //given
        String username = "로그예외_outerTxOff_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        //then: 멤버 저장, 로그 롤백
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: OFF
     * logRepository    @Transactional: OFF
     */
    @Test
    void SingleTx_success() {

        //given
        String username = "SingleTxOff_success";

        //when
        memberService.joinV1(username);

        //then: 모든 데이터가 정상 저장
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());

    }


    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: OFF
     * logRepository    @Transactional: OFF
     */
    @Test
    void SingleTx_fail() {

        //given
        String username = "로그예외_SingleTxOff_success";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        //then: 모든 데이터 롤백
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());

    }




}