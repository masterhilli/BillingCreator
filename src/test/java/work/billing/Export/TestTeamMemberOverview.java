package work.billing.Export;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class TestTeamMemberOverview {
    private TeamMemberOverviewMatrix teamMemberMatrix;
    private List<String> teamMembers;
    private final int fromPos = 1;
    private final int toPos = 3;
    @Before
    public void beforeTest () {
        teamMembers = new ArrayList<String>();
        teamMembers.add("Martin Hillbrand");
        teamMembers.add("Friedrich Schiller");
        teamMembers.add("Firdolin Bockwurst");
        teamMemberMatrix = new TeamMemberOverviewMatrix(teamMembers);
    }

    @Test
    public void putTeamMembersAndTimesOnMatrix_With3TeamMembers_ReturnsLastPos4() {
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(fromPos,toPos);

        Assert.assertEquals(4, teamMemberMatrix.lastPosition);
    }

    @Test
    public void putTeamMembersAndTimesOnMatrix_With3TeamMembers_ReturnsCorrectSUMIFAtPos2() {
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(fromPos,toPos);
        String sumIFVal = this.teamMemberMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.A.ordinal()).get(2);
        Assert.assertEquals("=SUMIF($B$1:$B$3,B2,$A$1:$A$3)", sumIFVal);
    }

    @Test
    public void putTeamMembersAndTimesOnMatrix_With3TeamMembers_ReturnsAll3TeamMembers() {
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(fromPos,toPos);
        List<String> actualTeamMembers = new ArrayList<>();
        for (int i = 1; i <= toPos; i++) {
            actualTeamMembers.add(this.teamMemberMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.B.ordinal()).get(i));
        }

        org.junit.Assert.assertArrayEquals(teamMembers.toArray(), actualTeamMembers.toArray());
    }

    @Test
    public void putTeamMembersAndTimesOnMatrix_With3TeamMembers_ReturnsCorrectSumFormula() {
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(fromPos,toPos);
        String actualSumFormulaAtTheEnd = this.teamMemberMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.A.ordinal()).get(4);

        Assert.assertEquals("=SUM(A1:A3)", actualSumFormulaAtTheEnd);
    }
}
