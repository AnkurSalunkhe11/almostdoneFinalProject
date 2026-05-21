Feature: EMI Calculator Tab UI Validation
  As a user of the Loan Calculator page
  I want to calculate my EMI by entering loan details
  So that I can validate the calculated Monthly EMI matches the expected value

  Background:
    Given I navigate to the Loan Calculator page
    And I read test data from sheet "Sheet1" and row 1 of "testdata.xlsx"

  @loancalculator @emicalculator
  Scenario: Validate inputs and monthly EMI output on the EMI Calculator tab
    When I select the EMI Calculator tab
    And I enter the loan details from Excel on the EMI Calculator tab
    Then I should verify input fields on the EMI Calculator tab match the Excel test data
    And I should verify the computed Monthly EMI matches the Excel test data
