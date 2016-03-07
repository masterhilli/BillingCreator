package work.billing.Cache;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import work.billing.Cache.Team.TeamMemberOverviewMatrix;
import work.billing.Spreadsheets.COL;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.AbstractMap;
import java.util.HashMap;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class IntegrationTestMonthlyReport {
    private MonthlyReportCache monthlyReport;

    private static final String TEST_3 = "Test3";
    private static final String TEST_2 = "Test2";
    private static final String TEST_1 = "Test1";
    private static final int TEST_VAL_1 = 10;
    private static final int TEST_VAL_2 = 20;
    private static final int TEST_VAL_3 = 30;
    private static final String PROJECT_1 = "Project1";
    private static final String PROJECT_2 = "Project2";
    private static final String PROJECT_3 = "Project3";
    private static final String MONTH_TRACKED = "2016-01";
    private AbstractMap<String, Integer> hourPerTeamMemberRate = new HashMap<>();
    private TrackedTimeSummary trackedTimeSum = new TrackedTimeSummary();

    @Before
    public void setUp() {
        hourPerTeamMemberRate.put(TEST_1, TEST_VAL_1);
        hourPerTeamMemberRate.put(TEST_2, TEST_VAL_2);
        hourPerTeamMemberRate.put(TEST_3, TEST_VAL_3);

        try {
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_1, MONTH_TRACKED, TEST_1, 10.10, 1.1, 11.11, TEST_VAL_1));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_2, MONTH_TRACKED, TEST_1, 10.10, 1.1, 11.11, TEST_VAL_1));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_3, MONTH_TRACKED, TEST_1, 10.10, 1.1, 11.11, TEST_VAL_1));

            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_1, MONTH_TRACKED, TEST_2, 20.20, 2.2, 22.22, TEST_VAL_2));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_2, MONTH_TRACKED, TEST_2, 20.20, 2.2, 22.22, TEST_VAL_2));

            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_3, MONTH_TRACKED, TEST_3, 30.30, 3.3, 33.33, TEST_VAL_3));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_1, MONTH_TRACKED, TEST_3, 30.30, 3.3, 33.33, TEST_VAL_3));
        } catch (TrackedTimeAlreadyExistsException e) {
            e.printStackTrace();
        }

        monthlyReport = new MonthlyReportCache(trackedTimeSum, hourPerTeamMemberRate);
        monthlyReport.setProjectLeadName(TEST_1);
        monthlyReport.initializeCache();
    }

    @Test
    public void TestInitializeOfMonthlyReport_ReturnsExpectedTeamMemberOverviewMatrix() {
        assertOnComparingHashMap(getExpectedTeamMemberOverViewMatrix().cellMatrix, getTeamMemberOverviewObject().cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedInternalProjectSummary() {
        assertOnComparingHashMap(getExpectedInternalProjectSummary().cellMatrix, getInternalProjectSummary().cellMatrix);
    }

    @Test
    public void TestInitializeOfMonthlyReport_ReturnsExpectedBillingProjectSummary() {
        assertOnComparingHashMap(getExpectedBillingProjectSummary().cellMatrix, getBillingProjectSummary().cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedInternalProject_1() {
        assertOnComparingHashMap(getExpectedInternalProjectMatrix_1().cellMatrix, getInternalProjectMatrix(PROJECT_1).cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedInternalProject_2() {
        assertOnComparingHashMap(getExpectedInternalProjectMatrix_2().cellMatrix, getInternalProjectMatrix(PROJECT_2).cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedInternalProject_3() {
        assertOnComparingHashMap(getExpectedInternalProjectMatrix_3().cellMatrix, getInternalProjectMatrix(PROJECT_3).cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedBillingProject_1() {
        assertOnComparingHashMap(getExpectedBillingProjectMatrix_1().cellMatrix, getBillingProjectMatrix(45).cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedBillingProject_2() {
        assertOnComparingHashMap(getExpectedBillingProjectMatrix_2().cellMatrix, getBillingProjectMatrix(38).cellMatrix);
    }

    @Ignore
    public void TestInitializeOfMonthlyReport_ReturnsExpectedBillingProject_3() {
        assertOnComparingHashMap(getExpectedBillingProjectMatrix_3().cellMatrix, getBillingProjectMatrix(53).cellMatrix);
    }

    private void assertOnComparingHashMap(AbstractMap<COL, AbstractMap<Integer, String>> expected, AbstractMap<COL, AbstractMap<Integer, String>> actual) {
        Assert.assertEquals(expected.keySet().size(), actual.keySet().size());
        for (COL col : expected.keySet()) {
            Assert.assertEquals(expected.get(col).keySet().size(), actual.get(col).size());
            for (Integer row : expected.get(col).keySet()) {
                Assert.assertEquals(expected.get(col).get(row), actual.get(col).get(row));
            }
        }
    }

    private BaseSpreadSheetMatrix getExpectedBillingProjectSummary() {
        BaseSpreadSheetMatrix billingPrjSum = new MockCellMatrix();
        billingPrjSum.putValueToMatrixAt(COL.B, 64, "=A53");
        billingPrjSum.putValueToMatrixAt(COL.B, 65, "Gesamtbetrag");
        billingPrjSum.putValueToMatrixAt(COL.B, 61, "Beschreibung");
        billingPrjSum.putValueToMatrixAt(COL.B, 62, "=A38");
        billingPrjSum.putValueToMatrixAt(COL.B, 63, "=A45");

        billingPrjSum.putValueToMatrixAt(COL.C, 64, "=C58");
        billingPrjSum.putValueToMatrixAt(COL.C, 65, "=SUM(C61:C64)");
        billingPrjSum.putValueToMatrixAt(COL.C, 61, "Netto");
        billingPrjSum.putValueToMatrixAt(COL.C, 62, "=C43");
        billingPrjSum.putValueToMatrixAt(COL.C, 63, "=C51");

        billingPrjSum.putValueToMatrixAt(COL.D, 64, "20%");
        billingPrjSum.putValueToMatrixAt(COL.D, 61, "Steuersatz");
        billingPrjSum.putValueToMatrixAt(COL.D, 62, "20%");
        billingPrjSum.putValueToMatrixAt(COL.D, 63, "20%");

        billingPrjSum.putValueToMatrixAt(COL.E, 64, "=E58");
        billingPrjSum.putValueToMatrixAt(COL.E, 65, "=SUM(E61:E64)");
        billingPrjSum.putValueToMatrixAt(COL.E, 61, "UST");
        billingPrjSum.putValueToMatrixAt(COL.E, 62, "=E43");
        billingPrjSum.putValueToMatrixAt(COL.E, 63, "=E51");

        billingPrjSum.putValueToMatrixAt(COL.F, 64, "=F58");
        billingPrjSum.putValueToMatrixAt(COL.F, 65, "=SUM(F61:F64)");
        billingPrjSum.putValueToMatrixAt(COL.F, 61, "Brutto");
        billingPrjSum.putValueToMatrixAt(COL.F, 62, "=F43");
        billingPrjSum.putValueToMatrixAt(COL.F, 63, "=F51");

        billingPrjSum.putValueToMatrixAt(COL.G, 65, "=if(C65<>SUM(D6:D28)/2,\"Betrag nicht gleich\",\"passt\")");

        return billingPrjSum;
    }

    private BaseSpreadSheetMatrix getExpectedInternalProjectSummary() {
        BaseSpreadSheetMatrix internalPrjSum = new MockCellMatrix();
        internalPrjSum.putValueToMatrixAt(COL.A, 29, "GESAMTBETRAG");

        internalPrjSum.putValueToMatrixAt(COL.B, 31, "Test1");

        internalPrjSum.putValueToMatrixAt(COL.C, 33, "Teamstunden");
        internalPrjSum.putValueToMatrixAt(COL.C, 34, "Summe für Rechnung");
        internalPrjSum.putValueToMatrixAt(COL.C, 35, "Effektiver Stundensatz");
        internalPrjSum.putValueToMatrixAt(COL.C, 30, "=if(D29<>D30,\"da stimmt was nicht\",\"Summen passen\")");
        internalPrjSum.putValueToMatrixAt(COL.C, 31, "Eigene Stunden");

        internalPrjSum.putValueToMatrixAt(COL.D, 33, "=A4-D31");
        internalPrjSum.putValueToMatrixAt(COL.D, 29, "=SUM(D1:D28)/2");
        internalPrjSum.putValueToMatrixAt(COL.D, 30, "=C43+C51+C58");
        internalPrjSum.putValueToMatrixAt(COL.D, 31, "=SUMIF($B$6:$B$28,B31,$A$6:$A$28)");

        internalPrjSum.putValueToMatrixAt(COL.E, 32, "gerundet");
        internalPrjSum.putValueToMatrixAt(COL.E, 33, "=IF(MOD(D33,50)=0,D33,(ROUNDDOWN(D33/50,0)+1)*50)");
        internalPrjSum.putValueToMatrixAt(COL.E, 29, "=SUM(E1:E28)/2");
        internalPrjSum.putValueToMatrixAt(COL.E, 31, "50");

        internalPrjSum.putValueToMatrixAt(COL.F, 33, "4.00");
        internalPrjSum.putValueToMatrixAt(COL.F, 29, "=SUM(F1:F28)/2");

        internalPrjSum.putValueToMatrixAt(COL.G, 33, "=MIN(E33*F33,G31)");
        internalPrjSum.putValueToMatrixAt(COL.G, 34, "=SUM(G31:G33)");
        internalPrjSum.putValueToMatrixAt(COL.G, 35, "=G34/D31");
        internalPrjSum.putValueToMatrixAt(COL.G, 31, "=D31*E31");
        return internalPrjSum;
    }

    private BaseSpreadSheetMatrix getExpectedTeamMemberOverViewMatrix() {
        BaseSpreadSheetMatrix teamMembers = new MockCellMatrix();
        teamMembers.putValueToMatrixAt(COL.A, 1, "=SUMIF($B$6:$B$28,B1,$A$6:$A$28)");
        teamMembers.putValueToMatrixAt(COL.B, 1, TEST_1);
        teamMembers.putValueToMatrixAt(COL.A, 2, "=SUMIF($B$6:$B$28,B2,$A$6:$A$28)");
        teamMembers.putValueToMatrixAt(COL.B, 2, TEST_3);
        teamMembers.putValueToMatrixAt(COL.A, 3, "=SUMIF($B$6:$B$28,B3,$A$6:$A$28)");
        teamMembers.putValueToMatrixAt(COL.B, 3, TEST_2);
        teamMembers.putValueToMatrixAt(COL.A, 4, "=SUM(A1:A3)");
        return teamMembers;
    }

    public BaseSpreadSheetMatrix getExpectedInternalProjectMatrix_1() {
        BaseSpreadSheetMatrix prjMatrix = new MockCellMatrix();
        prjMatrix.putValueToMatrixAt(COL.A, 16, "30.30");
        prjMatrix.putValueToMatrixAt(COL.A, 13, "project1");
        prjMatrix.putValueToMatrixAt(COL.A, 14, "10.10");
        prjMatrix.putValueToMatrixAt(COL.A, 15, "20.20");

        prjMatrix.putValueToMatrixAt(COL.B, 16, "Test3");
        prjMatrix.putValueToMatrixAt(COL.B, 17, "Reise- und Nächtigungskosten");
        prjMatrix.putValueToMatrixAt(COL.B, 14, "Test1");
        prjMatrix.putValueToMatrixAt(COL.B, 15, "Test2");

        prjMatrix.putValueToMatrixAt(COL.C, 16, "30");
        prjMatrix.putValueToMatrixAt(COL.C, 13, "Stundensatz");
        prjMatrix.putValueToMatrixAt(COL.C, 14, "10");
        prjMatrix.putValueToMatrixAt(COL.C, 15, "20");

        prjMatrix.putValueToMatrixAt(COL.D, 16, "=A16*C16");
        prjMatrix.putValueToMatrixAt(COL.D, 17, "6.60");
        prjMatrix.putValueToMatrixAt(COL.D, 18, "=SUM(D14:D17)");
        prjMatrix.putValueToMatrixAt(COL.D, 13, "Netto");
        prjMatrix.putValueToMatrixAt(COL.D, 14, "=A14*C14");
        prjMatrix.putValueToMatrixAt(COL.D, 15, "=A15*C15");

        prjMatrix.putValueToMatrixAt(COL.E, 16, "=D16*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 17, "=D17*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 18, "=SUM(E14:E17)");
        prjMatrix.putValueToMatrixAt(COL.E, 13, "UST");
        prjMatrix.putValueToMatrixAt(COL.E, 14, "=D14*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 15, "=D15*0.20");

        prjMatrix.putValueToMatrixAt(COL.F, 16, "=D16+E16");
        prjMatrix.putValueToMatrixAt(COL.F, 17, "=D17+E17");
        prjMatrix.putValueToMatrixAt(COL.F, 18, "=SUM(F14:F17)");
        prjMatrix.putValueToMatrixAt(COL.F, 13, "Brutto");
        prjMatrix.putValueToMatrixAt(COL.F, 14, "=D14+E14");
        prjMatrix.putValueToMatrixAt(COL.F, 15, "=D15+E15");

        return prjMatrix;
    }

    public BaseSpreadSheetMatrix getExpectedInternalProjectMatrix_2() {
        BaseSpreadSheetMatrix prjMatrix = new MockCellMatrix();

        prjMatrix.putValueToMatrixAt(COL.A, 6, "project2");
        prjMatrix.putValueToMatrixAt(COL.A, 7, "10.10");
        prjMatrix.putValueToMatrixAt(COL.A, 8, "20.20");

        prjMatrix.putValueToMatrixAt(COL.B, 7, "Test1");
        prjMatrix.putValueToMatrixAt(COL.B, 8, "Test2");
        prjMatrix.putValueToMatrixAt(COL.B, 9, "Reise- und Nächtigungskosten");

        prjMatrix.putValueToMatrixAt(COL.C, 6, "Stundensatz");
        prjMatrix.putValueToMatrixAt(COL.C, 7, "10");
        prjMatrix.putValueToMatrixAt(COL.C, 8, "20");

        prjMatrix.putValueToMatrixAt(COL.D, 6, "Netto");
        prjMatrix.putValueToMatrixAt(COL.D, 7, "=A7*C7");
        prjMatrix.putValueToMatrixAt(COL.D, 8, "=A8*C8");
        prjMatrix.putValueToMatrixAt(COL.D, 9, "3.30");
        prjMatrix.putValueToMatrixAt(COL.D, 10, "=SUM(D7:D9)");

        prjMatrix.putValueToMatrixAt(COL.E, 6, "UST");
        prjMatrix.putValueToMatrixAt(COL.E, 7, "=D7*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 8, "=D8*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 9, "=D9*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 10, "=SUM(E7:E9)");

        prjMatrix.putValueToMatrixAt(COL.F, 6, "Brutto");
        prjMatrix.putValueToMatrixAt(COL.F, 7, "=D7+E7");
        prjMatrix.putValueToMatrixAt(COL.F, 8, "=D8+E8");
        prjMatrix.putValueToMatrixAt(COL.F, 9, "=D9+E9");
        prjMatrix.putValueToMatrixAt(COL.F, 10, "=SUM(F7:F9)");

        return prjMatrix;
    }

    public BaseSpreadSheetMatrix getExpectedInternalProjectMatrix_3() {
        BaseSpreadSheetMatrix prjMatrix = new MockCellMatrix();

        prjMatrix.putValueToMatrixAt(COL.A, 21, "project3");
        prjMatrix.putValueToMatrixAt(COL.A, 22, "10.10");
        prjMatrix.putValueToMatrixAt(COL.A, 23, "30.30");

        prjMatrix.putValueToMatrixAt(COL.B, 22, "Test1");
        prjMatrix.putValueToMatrixAt(COL.B, 23, "Test3");
        prjMatrix.putValueToMatrixAt(COL.B, 24, "Reise- und Nächtigungskosten");

        prjMatrix.putValueToMatrixAt(COL.C, 21, "Stundensatz");
        prjMatrix.putValueToMatrixAt(COL.C, 22, "10");
        prjMatrix.putValueToMatrixAt(COL.C, 23, "30");

        prjMatrix.putValueToMatrixAt(COL.D, 21, "Netto");
        prjMatrix.putValueToMatrixAt(COL.D, 22, "=A22*C22");
        prjMatrix.putValueToMatrixAt(COL.D, 23, "=A23*C23");
        prjMatrix.putValueToMatrixAt(COL.D, 24, "4.40");
        prjMatrix.putValueToMatrixAt(COL.D, 25, "=SUM(D22:D24)");

        prjMatrix.putValueToMatrixAt(COL.E, 21, "UST");
        prjMatrix.putValueToMatrixAt(COL.E, 22, "=D22*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 23, "=D23*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 24, "=D24*0.20");
        prjMatrix.putValueToMatrixAt(COL.E, 25, "=SUM(E22:E24)");

        prjMatrix.putValueToMatrixAt(COL.F, 21, "Brutto");
        prjMatrix.putValueToMatrixAt(COL.F, 22, "=D22+E22");
        prjMatrix.putValueToMatrixAt(COL.F, 23, "=D23+E23");
        prjMatrix.putValueToMatrixAt(COL.F, 24, "=D24+E24");
        prjMatrix.putValueToMatrixAt(COL.F, 25, "=SUM(F22:F24)");
        return prjMatrix;
    }

    public BaseSpreadSheetMatrix getExpectedBillingProjectMatrix_1() {
        BaseSpreadSheetMatrix prjMatrix = new MockCellMatrix();
        prjMatrix.putValueToMatrixAt(COL.A, 48, "=A15");
        prjMatrix.putValueToMatrixAt(COL.A, 49, "=A16");
        prjMatrix.putValueToMatrixAt(COL.A, 45, "=A13");
        prjMatrix.putValueToMatrixAt(COL.A, 46, "Anz.");
        prjMatrix.putValueToMatrixAt(COL.A, 47, "=A14");

        prjMatrix.putValueToMatrixAt(COL.B, 48, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C15,\" EUR (\",B15,\")\")");
        prjMatrix.putValueToMatrixAt(COL.B, 49, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C16,\" EUR (\",B16,\")\")");
        prjMatrix.putValueToMatrixAt(COL.B, 50, "Reise- und Nächtigungskosten");
        prjMatrix.putValueToMatrixAt(COL.B, 46, "Beschreibung");
        prjMatrix.putValueToMatrixAt(COL.B, 47, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C14,\" EUR (\",B14,\")\")");

        prjMatrix.putValueToMatrixAt(COL.C, 48, "=ROUND(D15,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 49, "=ROUND(D16,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 50, "=ROUND(D17,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 51, "=SUM(C45:C50)");
        prjMatrix.putValueToMatrixAt(COL.C, 46, "Stundensatz");
        prjMatrix.putValueToMatrixAt(COL.C, 47, "=ROUND(D14,2)");

        prjMatrix.putValueToMatrixAt(COL.D, 48, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 49, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 50, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 46, "Steuersatz");
        prjMatrix.putValueToMatrixAt(COL.D, 47, "20%");

        prjMatrix.putValueToMatrixAt(COL.E, 48, "=ROUND(E15,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 49, "=ROUND(E16,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 50, "=ROUND(E17,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 51, "=SUM(E45:E50)");
        prjMatrix.putValueToMatrixAt(COL.E, 46, "UST");
        prjMatrix.putValueToMatrixAt(COL.E, 47, "=ROUND(E14,2)");

        prjMatrix.putValueToMatrixAt(COL.F, 48, "=ROUND(F15,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 49, "=ROUND(F16,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 50, "=ROUND(F17,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 51, "=SUM(F45:F50)");
        prjMatrix.putValueToMatrixAt(COL.F, 46, "Brutto");
        prjMatrix.putValueToMatrixAt(COL.F, 47, "=ROUND(F14,2)");

        prjMatrix.putValueToMatrixAt(COL.G, 51, "=if(C51<>D18,\"Betrag nicht gleich\",\"passt\")");

        return prjMatrix;
    }

    public BaseSpreadSheetMatrix getExpectedBillingProjectMatrix_2() {
        BaseSpreadSheetMatrix prjMatrix = new MockCellMatrix();

        prjMatrix.putValueToMatrixAt(COL.A, 38, "=A6");
        prjMatrix.putValueToMatrixAt(COL.A, 39, "Anz.");
        prjMatrix.putValueToMatrixAt(COL.A, 40, "=A7");
        prjMatrix.putValueToMatrixAt(COL.A, 41, "=A8");

        prjMatrix.putValueToMatrixAt(COL.B, 39, "Beschreibung");
        prjMatrix.putValueToMatrixAt(COL.B, 40, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C7,\" EUR (\",B7,\")\")");
        prjMatrix.putValueToMatrixAt(COL.B, 41, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C8,\" EUR (\",B8,\")\")");
        prjMatrix.putValueToMatrixAt(COL.B, 42, "Reise- und Nächtigungskosten");

        prjMatrix.putValueToMatrixAt(COL.C, 39, "Stundensatz");
        prjMatrix.putValueToMatrixAt(COL.C, 40, "=ROUND(D7,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 41, "=ROUND(D8,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 42, "=ROUND(D9,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 43, "=SUM(C38:C42)");

        prjMatrix.putValueToMatrixAt(COL.D, 39, "Steuersatz");
        prjMatrix.putValueToMatrixAt(COL.D, 40, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 41, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 42, "20%");

        prjMatrix.putValueToMatrixAt(COL.E, 39, "UST");
        prjMatrix.putValueToMatrixAt(COL.E, 40, "=ROUND(E7,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 41, "=ROUND(E8,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 42, "=ROUND(E9,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 43, "=SUM(E38:E42)");

        prjMatrix.putValueToMatrixAt(COL.F, 39, "Brutto");
        prjMatrix.putValueToMatrixAt(COL.F, 40, "=ROUND(F7,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 41, "=ROUND(F8,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 42, "=ROUND(F9,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 43, "=SUM(F38:F42)");

        prjMatrix.putValueToMatrixAt(COL.G, 43, "=if(C43<>D10,\"Betrag nicht gleich\",\"passt\")");

        return prjMatrix;
    }

    public BaseSpreadSheetMatrix getExpectedBillingProjectMatrix_3() {
        BaseSpreadSheetMatrix prjMatrix = new MockCellMatrix();
        prjMatrix.putValueToMatrixAt(COL.A, 53, "=A21");
        prjMatrix.putValueToMatrixAt(COL.A, 54, "Anz.");
        prjMatrix.putValueToMatrixAt(COL.A, 55, "=A22");
        prjMatrix.putValueToMatrixAt(COL.A, 56, "=A23");

        prjMatrix.putValueToMatrixAt(COL.B, 54, "Beschreibung");
        prjMatrix.putValueToMatrixAt(COL.B, 55, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C22,\" EUR (\",B22,\")\")");
        prjMatrix.putValueToMatrixAt(COL.B, 56, "=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C23,\" EUR (\",B23,\")\")");
        prjMatrix.putValueToMatrixAt(COL.B, 57, "Reise- und Nächtigungskosten");

        prjMatrix.putValueToMatrixAt(COL.C, 54, "Stundensatz");
        prjMatrix.putValueToMatrixAt(COL.C, 55, "=ROUND(D22,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 56, "=ROUND(D23,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 57, "=ROUND(D24,2)");
        prjMatrix.putValueToMatrixAt(COL.C, 58, "=SUM(C53:C57)");

        prjMatrix.putValueToMatrixAt(COL.D, 54, "Steuersatz");
        prjMatrix.putValueToMatrixAt(COL.D, 55, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 56, "20%");
        prjMatrix.putValueToMatrixAt(COL.D, 57, "20%");

        prjMatrix.putValueToMatrixAt(COL.E, 54, "UST");
        prjMatrix.putValueToMatrixAt(COL.E, 55, "=ROUND(E22,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 56, "=ROUND(E23,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 57, "=ROUND(E24,2)");
        prjMatrix.putValueToMatrixAt(COL.E, 58, "=SUM(E53:E57)");

        prjMatrix.putValueToMatrixAt(COL.F, 54, "Brutto");
        prjMatrix.putValueToMatrixAt(COL.F, 55, "=ROUND(F22,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 56, "=ROUND(F23,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 57, "=ROUND(F24,2)");
        prjMatrix.putValueToMatrixAt(COL.F, 58, "=SUM(F53:F57)");

        prjMatrix.putValueToMatrixAt(COL.G, 58, "=if(C58<>D25,\"Betrag nicht gleich\",\"passt\")");

        return prjMatrix;
    }

    private BaseSpreadSheetMatrix getBillingProjectMatrix(int headingRow) {
        for (BaseSpreadSheetMatrix matrix : monthlyReport.getSpreadSheetMatrixList()) {
            if (matrix instanceof work.billing.Cache.Billing.ProjectMatrix) {
                work.billing.Cache.Billing.ProjectMatrix prjMatrix = (work.billing.Cache.Billing.ProjectMatrix) matrix;
                if (prjMatrix.getHeadingRow() == headingRow) {
                    return matrix;
                }
            }
        }
        return null;
    }

    private BaseSpreadSheetMatrix getInternalProjectMatrix(String nameOfPrj) {
        for (BaseSpreadSheetMatrix matrix : monthlyReport.getSpreadSheetMatrixList()) {
            if (matrix instanceof work.billing.Cache.Internal.ProjectMatrix) {
                work.billing.Cache.Internal.ProjectMatrix prjMatrix = (work.billing.Cache.Internal.ProjectMatrix) matrix;
                if (prjMatrix.getProjectName().toLowerCase().compareTo(nameOfPrj.toLowerCase()) == 0) {
                    return matrix;
                }
            }
        }
        return null;
    }

    private BaseSpreadSheetMatrix getBillingProjectSummary() {
        for (BaseSpreadSheetMatrix matrix : monthlyReport.getSpreadSheetMatrixList()) {
            if (matrix instanceof work.billing.Cache.Billing.ProjectSummary) {
                return matrix;
            }
        }
        return null;
    }

    private BaseSpreadSheetMatrix getInternalProjectSummary() {
        for (BaseSpreadSheetMatrix matrix : monthlyReport.getSpreadSheetMatrixList()) {
            if (matrix instanceof work.billing.Cache.Internal.ProjectSummary) {
                return matrix;
            }
        }
        return null;
    }

    private BaseSpreadSheetMatrix getTeamMemberOverviewObject() {
        for (BaseSpreadSheetMatrix matrix : monthlyReport.getSpreadSheetMatrixList()) {
            if (matrix instanceof TeamMemberOverviewMatrix) {
                return matrix;
            }
        }
        return null;
    }

    private class MockCellMatrix extends BaseSpreadSheetMatrix {
        public void addCell(COL column, int row, String val) {
            putValueToMatrixAt(column, row, val);
        }
    }
}
