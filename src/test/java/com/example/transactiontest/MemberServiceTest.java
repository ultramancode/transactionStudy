package com.example.transactiontest;
import java.rmi.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;


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
    void singleTx_success() {

        //given
        String username = "singleTxOff_success";

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
    void singleTx_fail() {

        //given
        String username = "로그예외_singleTxOff_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        //then: 모든 데이터 롤백
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    @Test
    void outerTxOn_success() {

        //given
        String username = "outerTxOn_success";

        //when
        memberService.joinV1(username);

        //then: 모든 데이터가 정상 저장
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());

    }


    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    @Test
    void outerTxOn_fail() {

        //given
        String username = "로그예외_outerTxOn_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        //then: 모든 데이터 롤백
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */

    // propagation.Required(기본 값)에선 LogRepository에서 exception 터뜨리고, 서비스에서 트라이 캐치로 잡아서 처리 한다고 해도
    // 그냥 전체가 다 롤백되버린다.
    // 내부 트랜잭션에서 예외 발생하면 rollbackOnly를 외부 트랜잭션에 마킹해버리니
    // 트라이 캐치로 정상 흐름 처리해도 롤백 되버리는 것. + 이 경우엔 unexpectedRollbackException 터짐
    @Test
    void recoverException_fail() {

        //given
        String username = "로그예외_recoverException_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> memberService.joinV2(username)).isInstanceOf(UnexpectedRollbackException.class);

        //then: 모든 데이터 롤백
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON(propagation = REQUIRES_NEW) Exception
     */

    // Propagation.REQUIRES_NEW로 물리 트랜잭션 분리
    // LogRepository에서 쓰는 트랜잭션 안에서 db 커넥션도 별도로 사용하는 것
    // LogRepository 롤백 -> 예외를 서비스로 던짐 -> 서비스에서 트라이 캐치로 잡아서 정상 흐름 처리 -> 멤버는 커밋 ok
    @Test
    void recoverException_success() {

        //given
        String username = "로그예외_recoverException_success";

        memberService.joinV2(username);
        //then: 멤버 커밋, 로그 롤백
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());

    }





}