package com.dms.stepdefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import com.dms.browserInstance.BrowserHandle;
import com.dms.core.CoreFunctions;
import com.dms.dbconfig.Query;
import com.dms.pageobjects.AddInvoice_AdditionalDetailsPOM;
import com.dms.pageobjects.AddInvoice_FinancialInfo;
import com.dms.pageobjects.LoginPOM;
import com.dms.pageobjects.SearchInvoice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AdditionalDetailsStepDef {
	public static List<Map<String, String>> testData = new ArrayList<>();
	LoginPOM loginPOM = new LoginPOM();
	SearchInvoice searchInvoicePOM = new SearchInvoice();

	AddInvoice_AdditionalDetailsPOM additionalDetailsPOM = new AddInvoice_AdditionalDetailsPOM();
	AddInvoice_FinancialInfo financialInfoPOM = new AddInvoice_FinancialInfo();

	@When("User selects the {string} from old car offers")
	public void user_select_old_Car_offer(String option) {
		BrowserHandle.wait
				.until(ExpectedConditions.elementToBeClickable(additionalDetailsPOM.oldCarOfferNameDropdown()));
		CoreFunctions.click(additionalDetailsPOM.oldCarOfferNameDropdown(), option);
		BrowserHandle.wait.until(ExpectedConditions.elementToBeClickable(financialInfoPOM.chooseFromDropdown(option)));
		CoreFunctions.click(financialInfoPOM.chooseFromDropdown(option), option);

	}

	@When("Verify {string} pop up button is displayed")
	public void verify_popup_button_dispalyed(String btn) {
		BrowserHandle.wait.until(ExpectedConditions.visibilityOf(additionalDetailsPOM.popUpBtn(btn)));
		String text = CoreFunctions.getElementText(additionalDetailsPOM.popUpBtn(btn));
		Assert.assertEquals(text, btn);
	}

	@When("Verify {string} pop up button is not displayed")
	public void verify_popup_button_not_dispalyed(String btn) {
		Assert.assertFalse(additionalDetailsPOM.isPopupBtnDisplayed(btn),
				"Button '" + btn + "' should not be displayed");

	}

	@Then("Verify {string} is displayed")
	public void Verify_string_displayed(String text) {
		Assert.assertTrue(searchInvoicePOM.verifyByText(text));
	}

	@When("Click on {string} tab on Additional Details")
	public void click_tab_additioanl_Details(String btn) throws InterruptedException {
		CoreFunctions.moveToElement(additionalDetailsPOM.popUpBtn(btn));
		BrowserHandle.wait.until(ExpectedConditions.elementToBeClickable(additionalDetailsPOM.popUpBtn(btn)));
		CoreFunctions.click(additionalDetailsPOM.popUpBtn(btn), btn);
	}

	@When("user filled all details on Loyalty Bonus Benefits popup based on Vin Search for scenario {int}")
	public void user_Filled_on_loyalty_bonus(int rowNo) throws InterruptedException {
		rowNo--;
		CoreFunctions.click(additionalDetailsPOM.makeDropdown(), null);
		String text = AddInvoiceStepDef.testData.get(rowNo).get("Make").toString();
		CoreFunctions.click(financialInfoPOM.chooseFromDropdown(text), null);
		BrowserHandle.wait.until(ExpectedConditions.elementToBeClickable(additionalDetailsPOM.popUpBtn("VIN")));
		CoreFunctions.click(additionalDetailsPOM.VinSearchIcon(), null);
		BrowserHandle.wait.until(ExpectedConditions.elementToBeClickable(additionalDetailsPOM.enterValue_vin()));
		text = AddInvoiceStepDef.testData.get(rowNo).get("Vin").toString();
		CoreFunctions.setText(additionalDetailsPOM.enterValue_vin(), text);
		CoreFunctions.moveToElement(loginPOM.spanButton("SEARCH"));
		CoreFunctions.click(loginPOM.spanButton("SEARCH"), "SEARCH");
		BrowserHandle.wait.until(
				ExpectedConditions.elementToBeClickable(additionalDetailsPOM.chooseVehicleOnVehicalDetailsTab(text)));
		CoreFunctions.click(additionalDetailsPOM.chooseVehicleOnVehicalDetailsTab(text), null);
		CoreFunctions.click(additionalDetailsPOM.vinSearchOkBtn(), "OK");
		CoreFunctions.click(additionalDetailsPOM.policyTypeDropdown(), text);
		text = AddInvoiceStepDef.testData.get(rowNo).get("PolicyType").toString();
		CoreFunctions.click(financialInfoPOM.chooseFromDropdown(text), text);
		text = AddInvoiceStepDef.testData.get(rowNo).get("PolicyNo").toString();
		CoreFunctions.setText(additionalDetailsPOM.policyNo(), text);
		text = AddInvoiceStepDef.testData.get(rowNo).get("OldCarCustomerName").toString();
		CoreFunctions.setText(additionalDetailsPOM.oldCarCustomerName(), text);
		CoreFunctions.click(additionalDetailsPOM.relationDropdown(), "relation");
		text = AddInvoiceStepDef.testData.get(rowNo).get("Relation").toString();
		CoreFunctions.click(financialInfoPOM.chooseFromDropdown(text), text);
		CoreFunctions.click(loginPOM.spanButton("OK"), "OK");

	}

	@Then("Verify Prefilled fields for OrderId from scenario {int} on Additional Benefits tab")
	public void Verify_Prefilled_fields_for_OrderId_from_scenario_on_Additional_Benefits_tab(int rowNo)
			throws Exception {
		rowNo--;
		testData = CoreFunctions.test("InvoiceData");
		String orderId = testData.get(rowNo).get("OrderId").toString();
		String loyaltyRedeemPointBE = Query.get_fields_From_ShOrderBook("LOYL_REDEEM_PTS", "ORDER_NUM", orderId);
		String loyaltyRedeemPointFE = CoreFunctions.getElementAttribute(additionalDetailsPOM.loyaltyRedemptionPoints(),
				"value");
		Assert.assertEquals(loyaltyRedeemPointBE, loyaltyRedeemPointFE);

		String mobileNum = Query.get_fields_From_ShOrderBook("MOBILE", "ORDER_NUM", orderId);
		String cardType = Query.get_fields_From_ShOrderBook("REF_CARD_TYPE", "ORDER_NUM", orderId);
		String channel = Query.get_fields_From_GD_LOYALTY_ENROL("CHANNEL", "REG_MOBILE", mobileNum, "CARD_TYPE",
				cardType);
		String tier = Query.get_fields_From_GD_LOYALTY_ENROL("TIER", "REG_MOBILE", mobileNum, "CARD_TYPE", cardType);

		String valuePerPOint = Query.get_fields_From_GM_LOYALTY_MASTER("VALUE_PER_POINT", "CHANNEL", channel, "TIER",
				tier, "CARD_TYPE", cardType);
		int redeemAmountBE = ((int) Double.parseDouble(valuePerPOint)) * Integer.parseInt(loyaltyRedeemPointBE);

		String loyaltyReedemAmountFE = CoreFunctions.getElementAttribute(additionalDetailsPOM.loyaltyRedemptionAmount(),
				"value");
		Assert.assertEquals(Integer.toString(redeemAmountBE), loyaltyReedemAmountFE);
	}

	@When("Select make as {string}")
	public void select_make_as(String option) {
		CoreFunctions.click(additionalDetailsPOM.makeDropdown(), null);
		CoreFunctions.click(financialInfoPOM.chooseFromDropdown(option), null);

	}

	@Then("Verify if Vin button is disabled")
	public void Verify_if_vin_btn_disabled() {
		BrowserHandle.wait
				.until(ExpectedConditions.visibilityOf(additionalDetailsPOM.VinSearchIcon()));
		Assert.assertTrue(additionalDetailsPOM.VinSearchbtnDisabled().isDisplayed());
	}
	@Then("Verify error message if text box values are null")
	public void Text_box_value_Are_null() {
		CoreFunctions.clearText(additionalDetailsPOM.chassisNum());
		Assert.assertTrue(additionalDetailsPOM.chasisNoError().isDisplayed());
		
	}
	@When("Enter value manually in all fields")
	public void Enter_value_manually() {
		CoreFunctions.setText(additionalDetailsPOM.chassisNum(), "12345");
		CoreFunctions.setText(additionalDetailsPOM.regNum(), "12345");
		CoreFunctions.setText(additionalDetailsPOM.engineNo(), "12345");
		CoreFunctions.setText(additionalDetailsPOM.model(), "12345");
		CoreFunctions.setText(additionalDetailsPOM.vairant(), "12345");
		CoreFunctions.setText(additionalDetailsPOM.vin(), "12345");
		CoreFunctions.setText(additionalDetailsPOM.oldCarCustomerName(), "12345");
	}
	@Then("Verify if fields are disabled")
	public void Verify_filed_disabled() {
		BrowserHandle.wait
		.until(ExpectedConditions.attributeToBe(additionalDetailsPOM.chassisNum(), "disabled", "true"));
System.out.println(additionalDetailsPOM.chassisNum().getAttribute("disabled"));
Assert.assertEquals(additionalDetailsPOM.chassisNum().getAttribute("disabled"), "true");

	}
}