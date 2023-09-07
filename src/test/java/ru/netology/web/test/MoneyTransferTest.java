package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.VerificationPage;
import ru.netology.web.page.TransferPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

public class MoneyTransferTest {
    private DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        VerificationPage verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);

    }
        @Test
        void shouldTransferFromFirstToSecond() {
            var firstCardInfo = getFirstCardInfo();
            var secondCardInfo = getSecondCardInfo();
            var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
            var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
            var amount = generateValidAmount(firstCardBalance);
            var expectedBalanceFirstCard = firstCardBalance - amount;
            var expectedBalanceSecondCard = secondCardBalance + amount;

            TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
            dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);

            var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
            var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);

            assertEquals(expectedBalanceFirstCard,actualBalanceFirstCard);
            assertEquals(expectedBalanceSecondCard,actualBalanceSecondCard);

        }

        @Test
        void shouldGetErrorMessageIfAmountExceedsBalance() {
            var firstCardInfo = getFirstCardInfo();
            var secondCardInfo = getSecondCardInfo();
            var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
            var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
            var amount = generateInvalidAmount(secondCardBalance);

            TransferPage transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
            transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
            transferPage.findErrorMessage("Вы пытаетесь провести перевод суммы, превышающей остаток на карте списания");

            var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
            var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);

            assertEquals(firstCardBalance,actualBalanceFirstCard);
            assertEquals(secondCardBalance,actualBalanceSecondCard);
        }
    }