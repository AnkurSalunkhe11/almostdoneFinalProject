Feature: Car Loan EMI Calculation and Validation
  As a user
  I want to calculate car loan EMI for a given loan amount, interest rate, and tenure
  So that I can validate the monthly EMI and first month interest/principal breakdown

  @carloan @datadriven
  Scenario Outline: Validate Car Loan EMI for various loan amounts, interest rates, and tenures
    Given I navigate to the EMI calculator homepage
    When I select the Car Loan tab
    And I enter loan amount as "<LoanAmount>"
    And I enter interest rate as "<InterestRate>"
    And I enter tenure as "<Tenure>" year
    Then I should see the calculated monthly EMI displayed
    And I should see the first month interest amount
    And I should see the first month principal amount
    And I print the EMI details for verification

    Examples:
      | LoanAmount | InterestRate | Tenure | Description              |
      | 1500000    | 9.5          | 1      | 15 Lac / 9.5% / 1 Year   |


