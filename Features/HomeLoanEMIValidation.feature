Feature: Home Loan EMI Calculation and Year-on-Year Table Extraction
  As a user
  I want to calculate home loan EMI for given loan amount, interest rate, and tenure
  So that I can extract the year-on-year amortization table data and store it in Excel

  @homeloan @datadriven
  Scenario Outline: Extract Home Loan year-on-year table for various loan scenarios
    Given I navigate to the EMI calculator homepage
    When I select the Home Loan tab
    And I enter home loan amount as "<LoanAmount>"
    And I enter home loan interest rate as "<InterestRate>"
    And I enter home loan tenure as "<Tenure>" years
    And the page loads with amortization table
    And I scroll down to the amortization table
    Then I should extract all year-on-year amortization data including monthly breakdowns
    And the data should be exported to an Excel file named "<FileName>"
    And the Excel file should be saved successfully

    Examples:
      | LoanAmount | InterestRate | Tenure | FileName                          |
      | 1500000    | 9.5          | 1      | HomeLoan_15Lac_9_5_1Year.xlsx     |
