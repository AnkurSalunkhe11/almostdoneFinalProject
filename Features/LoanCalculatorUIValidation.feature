Feature: Loan Calculator Tabs UI Validation
  As a user of the Loan Calculator page
  I want to compute loan amount and tenure by entering details
  So that I can validate the calculated outputs match the expected values

  Background:
    Given I navigate to the Loan Calculator page
    And I read test data from sheet "Sheet1" and row 1 of "testdata.xlsx"

  @loancalculator @loanamountcalculator
  Scenario: Validate inputs and total loan amount output on the Loan Amount Calculator tab
    When I select the Loan Amount Calculator tab
    And I enter the loan details from Excel on the Loan Amount Calculator tab
    Then I should verify input fields on the Loan Amount Calculator tab match the Excel test data
    And I should verify the computed Total Loan Amount matches the Excel test data

  @loancalculator @loantenurecalculator
  Scenario: Validate inputs and tenure output on the Loan Tenure Calculator tab
    When I select the Loan Tenure Calculator tab
    And I enter the loan details from Excel on the Loan Tenure Calculator tab
    Then I should verify input fields on the Loan Tenure Calculator tab match the Excel test data
    And I should verify the computed Loan Tenure matches the Excel test data
