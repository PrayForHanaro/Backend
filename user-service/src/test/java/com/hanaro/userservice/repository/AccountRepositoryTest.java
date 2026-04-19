package com.hanaro.userservice.repository;

import com.hanaro.userservice.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class AccountRepositoryTest extends BaseRepositoryTest {
    private static Long id;
    private static long orgCount = 0;
    private static final String testPhone = "010-" + (System.nanoTime() % 100000000L);

    @Autowired
    private AccountRepository repository;
    @Autowired
    private UserRepository userRepository;

    private static User savedUser;

    @BeforeEach
    void setOrgCount() {
        orgCount = repository.count();
        if (savedUser == null) {
            savedUser = userRepository.save(User.builder()
                .name("주인")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phone(testPhone)
                .password("pwd")
                .build());
        }
    }

    @Test
    @Order(1)
    void writeTest() {
        Account newAccount = Account.builder()
            .user(savedUser)
            .bankName("하나은행")
            .accountNumber("ACC-" + System.nanoTime() % 100000000L)
            .balance(BigDecimal.valueOf(1000))
            .isHana(true)
            .isDefault(false)
            .isSavings(false)
            .build();
        Account saved = repository.save(newAccount);
        id = saved.getAccountId();
        System.out.println("saved id = " + id);
    }

    @Test
    @Order(2)
    void readTest() {
        Account found = repository.findById(id).orElseThrow();
        assertThat(found.getBankName()).isEqualTo("하나은행");
        System.out.println("found = " + found.getBankName());
    }

    @Test
    @Order(3)
    void updateTest() {
        Account found = repository.findById(id).orElseThrow();


        Account updated = Account.builder()
            .accountId(found.getAccountId())
            .user(found.getUser())
            .bankName(found.getBankName())
            .accountNumber(found.getAccountNumber())
            .balance(BigDecimal.valueOf(2000))
            .isHana(true)
            .isDefault(false)
            .isSavings(false)
            .version(found.getVersion())
            .build();
        repository.save(updated);
        repository.flush();

        assertThat(repository.findById(id).get().getBalance())
            .isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    @Order(4)
    void deleteTest() {
        repository.deleteById(id);
        repository.flush();
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @Order(5)
    void finalCheck() {
        assertThat(repository.count()).isEqualTo(orgCount);
        System.out.println("finalCheck count = " + repository.count());
    }
}