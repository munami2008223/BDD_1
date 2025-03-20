package ru.netology.service.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.service.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private SelenideElement codeInputField = $("[data-test-id='code'] input");
    private SelenideElement verifiButton = $("[data-test-id='action-verify']");

    public VerificationPage() {
        codeInputField.shouldBe(Condition.visible);
    }

    public DashBoardPage validVerify(DataHelper.VerificationCode verificationCode) {
        codeInputField.setValue(verificationCode.getCode());
        verifiButton.click();
        return new DashBoardPage();
    }

}
