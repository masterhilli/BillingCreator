package work.billing.Export;

import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;

import java.util.List;

/**
 * Created by mhillbrand on 3/3/2016.
 * Holds the first lines, where the sum of all effort is hold for each team member and overall
 */
public class TeamMemberOverviewMatrix extends BaseSpreadSheetMatrix {
    public List<String> teamMembers;
    protected int lastPosition = 0;
    private final int START_POS = 1;

    public TeamMemberOverviewMatrix(List<String> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void putTeamMembersAndTimesToMatrix(int fromPos, int toPos) {
        int curPos = START_POS; // we always start at the first Pos. We might make that configurable in the future!
        for (String teamMember : this.teamMembers) {
            String val = SpreadsheetFormulas.SUMIF(COL.B.toString(), COL.A.toString(),
                                                    fromPos, toPos, COL.B.toString(), curPos);
            putValueToMatrixAt(COL.A, curPos, val);
            putValueToMatrixAt(COL.B, curPos++, teamMember);
        }
        putValueToMatrixAt(COL.A, curPos, SpreadsheetFormulas.SUM(COL.A, START_POS, curPos-1));
        lastPosition = curPos;
    }
}
