>>SOURCE FORMAT FREE
IDENTIFICATION DIVISION.
PROGRAM-ID. PAYROLL-CALCULATION-ENGINE.

ENVIRONMENT DIVISION.
CONFIGURATION SECTION.
SOURCE-COMPUTER. GNUCOBOL.
OBJECT-COMPUTER. GNUCOBOL.

DATA DIVISION.
WORKING-STORAGE SECTION.
*> The first group stores the raw text that arrives from the Java process.
*> Reading text first keeps the input contract simple and lets us convert data
*> in one carefully named step later in the program.
01 employeeIdentifierText                PIC X(20).
01 employeeFullNameText                  PIC X(60).
01 hourlyWageAmountText                  PIC X(20).
01 regularHoursWorkedText                PIC X(20).
01 overtimeHoursWorkedText               PIC X(20).
01 standardTaxRatePercentageText         PIC X(20).
01 benefitDeductionAmountText            PIC X(20).
01 performanceBonusAmountText            PIC X(20).

*> The second group stores the numeric values that payroll mathematics requires.
01 hourlyWageAmount                      PIC 9(5)V99 VALUE ZERO.
01 regularHoursWorked                    PIC 9(3)V99 VALUE ZERO.
01 overtimeHoursWorked                   PIC 9(3)V99 VALUE ZERO.
01 standardTaxRatePercentage             PIC 9V9999 VALUE ZERO.
01 benefitDeductionAmount                PIC 9(5)V99 VALUE ZERO.
01 performanceBonusAmount                PIC 9(5)V99 VALUE ZERO.
01 overtimePayMultiplier                 PIC 9V99 VALUE 1.50.
01 retirementContributionRate            PIC 9V9999 VALUE 0.0500.
01 paidLeaveAccrualPerWorkedHour         PIC 9V9999 VALUE 0.0385.

*> These variables hold each business result before we format it for output.
01 grossRegularPayAmount                 PIC 9(7)V99 VALUE ZERO.
01 grossOvertimePayAmount                PIC 9(7)V99 VALUE ZERO.
01 grossPayAmount                        PIC 9(7)V99 VALUE ZERO.
01 taxWithholdingAmount                  PIC 9(7)V99 VALUE ZERO.
01 retirementContributionAmount          PIC 9(7)V99 VALUE ZERO.
01 paidLeaveAccruedHours                 PIC 9(3)V99 VALUE ZERO.
01 netPayAmount                          PIC 9(7)V99 VALUE ZERO.

*> Edited fields make the displayed values easy for the Java side to parse.
01 grossRegularPayAmountDisplay          PIC Z(7).99.
01 grossOvertimePayAmountDisplay         PIC Z(7).99.
01 grossPayAmountDisplay                 PIC Z(7).99.
01 taxWithholdingAmountDisplay           PIC Z(7).99.
01 retirementContributionAmountDisplay   PIC Z(7).99.
01 benefitDeductionAmountDisplay         PIC Z(7).99.
01 paidLeaveAccruedHoursDisplay          PIC Z(3).99.
01 netPayAmountDisplay                   PIC Z(7).99.

*> A simple error message channel lets the Java client detect bad input.
01 errorMessageText                      PIC X(120) VALUE SPACES.

PROCEDURE DIVISION.
    PERFORM collectPayrollInputData
    PERFORM validatePayrollInputData

    IF errorMessageText NOT = SPACES
        PERFORM displayErrorResult
        MOVE 1 TO RETURN-CODE
        GOBACK
    END-IF

    PERFORM convertInputTextIntoNumericValues
    PERFORM calculatePayrollAmounts
    PERFORM displayPayrollResult
    GOBACK.

collectPayrollInputData.
    *> The Java client writes one value per line. We read them in the same
    *> order so the contract is explicit and beginner-friendly.
    ACCEPT employeeIdentifierText
    ACCEPT employeeFullNameText
    ACCEPT hourlyWageAmountText
    ACCEPT regularHoursWorkedText
    ACCEPT overtimeHoursWorkedText
    ACCEPT standardTaxRatePercentageText
    ACCEPT benefitDeductionAmountText
    ACCEPT performanceBonusAmountText.

validatePayrollInputData.
    *> This first validation step catches the most important structural errors
    *> before arithmetic begins.
    IF FUNCTION TRIM(employeeIdentifierText) = SPACES
        MOVE "Employee identifier was empty." TO errorMessageText
    END-IF

    IF FUNCTION TRIM(employeeFullNameText) = SPACES
        MOVE "Employee full name was empty." TO errorMessageText
    END-IF.

convertInputTextIntoNumericValues.
    *> NUMVAL converts text such as 42.50 into the numeric format COBOL can use
    *> for calculations. The separation from input reading makes debugging much
    *> easier for a new maintainer.
    COMPUTE hourlyWageAmount = FUNCTION NUMVAL(hourlyWageAmountText)
    COMPUTE regularHoursWorked = FUNCTION NUMVAL(regularHoursWorkedText)
    COMPUTE overtimeHoursWorked = FUNCTION NUMVAL(overtimeHoursWorkedText)
    COMPUTE standardTaxRatePercentage = FUNCTION NUMVAL(standardTaxRatePercentageText)
    COMPUTE benefitDeductionAmount = FUNCTION NUMVAL(benefitDeductionAmountText)
    COMPUTE performanceBonusAmount = FUNCTION NUMVAL(performanceBonusAmountText).

calculatePayrollAmounts.
    *> Regular pay is the standard hourly wage multiplied by regular hours.
    COMPUTE grossRegularPayAmount ROUNDED =
        hourlyWageAmount * regularHoursWorked

    *> Overtime pay uses the overtime multiplier because overtime work is paid
    *> at one and one-half times the base hourly wage in this demonstration.
    COMPUTE grossOvertimePayAmount ROUNDED =
        hourlyWageAmount * overtimeHoursWorked * overtimePayMultiplier

    *> Gross pay is the total money earned before any deductions are removed.
    COMPUTE grossPayAmount ROUNDED =
        grossRegularPayAmount
        + grossOvertimePayAmount
        + performanceBonusAmount

    *> Tax withholding is a simple demonstration percentage rather than a full
    *> tax table implementation.
    COMPUTE taxWithholdingAmount ROUNDED =
        grossPayAmount * standardTaxRatePercentage

    *> Retirement contribution is modeled as a fixed percentage to keep the
    *> example approachable.
    COMPUTE retirementContributionAmount ROUNDED =
        grossPayAmount * retirementContributionRate

    *> Paid leave accrual is based on total worked hours.
    COMPUTE paidLeaveAccruedHours ROUNDED =
        (regularHoursWorked + overtimeHoursWorked) * paidLeaveAccrualPerWorkedHour

    *> Net pay is the final take-home pay after deductions.
    COMPUTE netPayAmount ROUNDED =
        grossPayAmount
        - taxWithholdingAmount
        - retirementContributionAmount
        - benefitDeductionAmount.

displayPayrollResult.
    *> We move raw numeric fields into edited display fields so the Java process
    *> receives predictable decimal text.
    MOVE grossRegularPayAmount TO grossRegularPayAmountDisplay
    MOVE grossOvertimePayAmount TO grossOvertimePayAmountDisplay
    MOVE grossPayAmount TO grossPayAmountDisplay
    MOVE taxWithholdingAmount TO taxWithholdingAmountDisplay
    MOVE retirementContributionAmount TO retirementContributionAmountDisplay
    MOVE benefitDeductionAmount TO benefitDeductionAmountDisplay
    MOVE paidLeaveAccruedHours TO paidLeaveAccruedHoursDisplay
    MOVE netPayAmount TO netPayAmountDisplay

    DISPLAY "employeeIdentifier=" FUNCTION TRIM(employeeIdentifierText)
    DISPLAY "employeeFullName=" FUNCTION TRIM(employeeFullNameText)
    DISPLAY "grossRegularPayAmount=" FUNCTION TRIM(grossRegularPayAmountDisplay)
    DISPLAY "grossOvertimePayAmount=" FUNCTION TRIM(grossOvertimePayAmountDisplay)
    DISPLAY "grossPayAmount=" FUNCTION TRIM(grossPayAmountDisplay)
    DISPLAY "taxWithholdingAmount=" FUNCTION TRIM(taxWithholdingAmountDisplay)
    DISPLAY "retirementContributionAmount=" FUNCTION TRIM(retirementContributionAmountDisplay)
    DISPLAY "benefitDeductionAmount=" FUNCTION TRIM(benefitDeductionAmountDisplay)
    DISPLAY "paidLeaveAccruedHours=" FUNCTION TRIM(paidLeaveAccruedHoursDisplay)
    DISPLAY "netPayAmount=" FUNCTION TRIM(netPayAmountDisplay).

displayErrorResult.
    DISPLAY "errorMessage=" FUNCTION TRIM(errorMessageText).
