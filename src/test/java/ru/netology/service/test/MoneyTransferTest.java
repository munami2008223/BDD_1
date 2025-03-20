package ru.netology.service.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.service.data.DataHelper;
import ru.netology.service.page.DashBoardPage;
import ru.netology.service.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;

import static ru.netology.service.data.DataHelper.*;


public class MoneyTransferTest {
    DashBoardPage dashBoardPage;
    CardInfo firstCardInfo;
    CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;


    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashBoardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashBoardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashBoardPage.getCardBalance(secondCardInfo);
    }

    @Test
    @DisplayName("Перевести деньги с первой карты на вторую")
    void shouldTransferFromFirstToSecond() {
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashBoardPage.selectCardToTransfer(secondCardInfo);
        dashBoardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashBoardPage.reloadDashboardPage();
        assertAll(() -> dashBoardPage.checkCardBalance(firstCardInfo, expectedBalanceFirstCard),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, expectedBalanceSecondCard));
    }

    @Test
    @DisplayName("Перевести деньги сo второй карты на первую")
    void shouldTransferFromSecondToFirst() {
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance + amount;
        var expectedBalanceSecondCard = secondCardBalance - amount;
        var transferPage = dashBoardPage.selectCardToTransfer(firstCardInfo);
        dashBoardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCardInfo);
        dashBoardPage.reloadDashboardPage();
        assertAll(() -> dashBoardPage.checkCardBalance(firstCardInfo, expectedBalanceFirstCard),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, expectedBalanceSecondCard));
    }


    @Test
    @DisplayName("Сообщить об ошибке, если сумма перевода больше баланса")
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashBoardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        assertAll(() -> transferPage.findErrorMessage("Ошибка! введена сумма, превышающая остаток на карте списания"),
                () -> dashBoardPage.reloadDashboardPage(),
                () -> dashBoardPage.checkCardBalance(firstCardInfo, firstCardBalance),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, secondCardBalance));
    }

}
