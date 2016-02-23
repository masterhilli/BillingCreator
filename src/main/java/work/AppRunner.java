package work;


import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;
import work.billing.Setting.HourRate;
import work.billing.Spreadsheets.ProjectRowReference;
import work.billing.Spreadsheets.ProjectSummarySpreadsheetUpdater;
import work.billing.Spreadsheets.ProjectsheetToTrackTimeMapper;
import work.billing.Spreadsheets.Spreadsheet;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppRunner {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Please provide 2 args: <worksheetname> <pathToSettingsFile>");
            return;
        }

        FileSettings settings = FileSettingReader.ReadFileSettingsFromFile(args[1]);

        createBillingSpreadsheet(args[0], settings);


        // no glue if we still will need them.
        //TestMethodsForSpreadSheets();
    }

    private static void createBillingSpreadsheet(String worksheetName, FileSettings settings) {

        TrackedTimeSummary trackedTimeSum = new TrackedTimeSummary();
        for (String key : settings.importFileId) {
            Spreadsheet timeSheet = new Spreadsheet(key, worksheetName);
            TrackedTime timeTracked = ProjectsheetToTrackTimeMapper.createTrackedTimeFromSpreadsheet(
                    timeSheet, worksheetName, settings.getHourRateAsHashMapPerTeamMember());
            try {
                trackedTimeSum.addTrackedTime(timeTracked);
            } catch (TrackedTimeAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
        //trackedTimeSum.printTimesForAllProjects();
        //trackedTimeSum.printTimesForAllTeamMembers();
        int startPos = settings.personHourCosts.size()+3;
        startPos = writeProjectsToSpreadsheet(worksheetName, settings, trackedTimeSum, startPos);
        writeSumOfTimesPerTeamMembersToSpreadsheet(worksheetName,settings, 1,  startPos);
        int endOfSearch = startPos;
        writeSummaryAtTheEndOfTheList(worksheetName, settings, startPos+2, listOfSums, settings.personHourCosts.size()+1, endOfSearch);

        startPos = writeProjectInformationAgain(worksheetName, settings, startPos + 10);

        Spreadsheet exportSpreadsheet = new Spreadsheet(settings.exportFileId);
        writeProjectShortSummary(worksheetName, settings, startPos+2);
        String valueForSumOfProjects = "=";
        for (ProjectRowReference prjRowRef : rowsForRerferencesPerProject) {
            if (valueForSumOfProjects.length() != 1) {
                valueForSumOfProjects+="+";
            }
            valueForSumOfProjects += String.format("C%d", prjRowRef.getSumRowFor2ndRun());
        }
        exportSpreadsheet.insertValueIntoCell(worksheetName,4, posForSumUp, valueForSumOfProjects);
    }

    private static void writeProjectShortSummary(String worksheetName, FileSettings settings, int startPos) {
        Spreadsheet exportSpreadsheet = new Spreadsheet(settings.exportFileId);
        exportSpreadsheet.insertValueIntoCell(worksheetName, 2, startPos, "Beschreibung");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, startPos, "Netto");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 4, startPos, "Steuersatz");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 5, startPos, "Ust.");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 6, startPos, "Brutto");
        startPos++;
        int sumStartPos = startPos;
        for (ProjectRowReference prjRowInfo: rowsForRerferencesPerProject) {
            exportSpreadsheet.insertValueIntoCell(worksheetName, 2, startPos, String.format("=A%d", prjRowInfo.getProjectNameRow2nd()));
            exportSpreadsheet.insertValueIntoCell(worksheetName, 3, startPos, String.format("=C%d", prjRowInfo.getSumRowFor2ndRun()));
            exportSpreadsheet.insertValueIntoCell(worksheetName, 4, startPos, "20%");
            exportSpreadsheet.insertValueIntoCell(worksheetName, 5, startPos, String.format("=E%d", prjRowInfo.getSumRowFor2ndRun()));
            exportSpreadsheet.insertValueIntoCell(worksheetName, 6, startPos, String.format("=F%d", prjRowInfo.getSumRowFor2ndRun()));
            startPos++;
        }

        exportSpreadsheet.insertValueIntoCell(worksheetName, 2, startPos, "Gesamtbetrag");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, startPos, String.format("=SUM(C%d:C%d)", sumStartPos, startPos-1));

        exportSpreadsheet.insertValueIntoCell(worksheetName, 5, startPos, String.format("=SUM(E%d:E%d)", sumStartPos, startPos-1));
        exportSpreadsheet.insertValueIntoCell(worksheetName, 6, startPos, String.format("=SUM(F%d:F%d)", sumStartPos, startPos-1));

        int minRegion = Integer.MAX_VALUE;
        int maxRegion = Integer.MIN_VALUE;
        for (ProjectRowReference prjRowRef : rowsForRerferencesPerProject) {
            minRegion = Integer.min(minRegion, prjRowRef.getProjectNameRow());
            maxRegion = Integer.max(maxRegion, prjRowRef.getSumRow());
        }
        exportSpreadsheet.insertValueIntoCell(worksheetName, 7, startPos,
                String.format("=if(C%d<>sum(D%d:D%d)/2,\"Betrag nicht gleich\",\"passt\")", startPos, minRegion, maxRegion));

    }

    private static int writeProjectInformationAgain(String worksheetName, FileSettings settings, int startPos) {
        Spreadsheet exportSpreadsheet = new Spreadsheet(settings.exportFileId);
        for (ProjectRowReference prjRowRef : rowsForRerferencesPerProject){
            prjRowRef.setProjectNameRow2nd(startPos);
            exportSpreadsheet.insertValueIntoCell(worksheetName, 1, startPos++, String.format("=A%d", prjRowRef.getProjectNameRow()));
            int headPos = startPos;
            writeFirstLine(exportSpreadsheet, worksheetName, startPos++);
            startPos = writeUsers(exportSpreadsheet,worksheetName, prjRowRef, startPos);
            writeTravelCosts(exportSpreadsheet, worksheetName, prjRowRef, startPos++);
            writeSums(exportSpreadsheet, worksheetName, prjRowRef, startPos++, headPos);
            exportSpreadsheet.insertValueIntoCell(worksheetName, 7, startPos,
                    String.format("=if(C%d<>D%d,\"Betrag nicht gleich\",\"passt\")", startPos-1, prjRowRef.getSumRow()));
            startPos++;
        }
        return startPos;
    }

    private static void writeSums(Spreadsheet exportSpreadsheet, String worksheetName, ProjectRowReference prjRowRef, int startPos, int headPos) {
        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, startPos,
                String.format("=SUM(C%d:C%d)", headPos, startPos-1));
        exportSpreadsheet.insertValueIntoCell(worksheetName, 5, startPos,
                String.format("=SUM(E%d:F%d)", headPos, startPos-1));
        exportSpreadsheet.insertValueIntoCell(worksheetName, 6, startPos,
                String.format("=SUM(F%d:F%d)", headPos, startPos-1));
        prjRowRef.setSumRowFor2ndRun(startPos);
    }

    private static void writeTravelCosts(Spreadsheet exportSpreadsheet, String worksheetName, ProjectRowReference prjRowRef, int startPos) {
        exportSpreadsheet.insertValueIntoCell(worksheetName, 2, startPos, "Reise und Nächtigungskosten");
        writeCalculations(exportSpreadsheet, worksheetName, startPos, prjRowRef.getTravelCostRow());
    }

    private static int writeUsers(Spreadsheet exportSpreadsheet, String worksheetName, ProjectRowReference prjRowRef, int startPos) {
        int i = 0;
        int to = (prjRowRef.getTravelCostRow()- prjRowRef.getProjectNameRow())-1;
        for ( i=0; i < to; i++)
        {
            int rowOfUser = prjRowRef.getProjectNameRow()+(i+1);
            exportSpreadsheet.insertValueIntoCell(worksheetName, 1, startPos+i, String.format("=A%d", rowOfUser));
            exportSpreadsheet.insertValueIntoCell(worksheetName, 2, startPos+i,
                    String.format("=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C%d,\" EUR (\",B%d,\")\")", rowOfUser, rowOfUser));
            writeCalculations(exportSpreadsheet, worksheetName, startPos+i, rowOfUser);
        }
        return startPos+i;
    }

    private static void writeCalculations(Spreadsheet exportSpreadsheet, String worksheetName, int startPos, int rowOfData) {
        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, startPos, String.format("=ROUND(D%d,2)", rowOfData));
        exportSpreadsheet.insertValueIntoCell(worksheetName, 4, startPos, "20%");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 5, startPos, String.format("=ROUND(E%d,2)", rowOfData));
        exportSpreadsheet.insertValueIntoCell(worksheetName, 6, startPos, String.format("=ROUND(F%d,2)", rowOfData));
    }

    private static void writeFirstLine(Spreadsheet exportSpreadsheet, String worksheetName, int startPos) {
        exportSpreadsheet.insertValueIntoCell(worksheetName, 1, startPos, "Anz.");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 2, startPos, "Beschreibung");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, startPos, "Netto");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 4, startPos, "Steuersatz");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 5, startPos, "Ust.");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 6, startPos, "Brutto");
    }

    private static List<Integer> listOfSums = new ArrayList<>();
    private static void writeSumOfTimesPerTeamMembersToSpreadsheet(String worksheetName, FileSettings settings,
                                                                   int startPos, int endPosOfProjects) {
        Spreadsheet exportSpreadsheet = new Spreadsheet(settings.exportFileId);
        int start = startPos;
        for (HourRate hourRatePerTeamMember: settings.personHourCosts) {
            String value = getFormatForSUMIF(settings.getHourRateAsHashMapPerTeamMember().size()+1, endPosOfProjects, startPos);
            exportSpreadsheet.insertValueIntoCell(worksheetName,1, startPos, value);
            exportSpreadsheet.insertValueIntoCell(worksheetName,2, startPos++, hourRatePerTeamMember.name);
        }
        exportSpreadsheet.insertValueIntoCell(worksheetName,1,startPos, String.format("=SUM(A%d:A%d)", start, startPos-1));
    }

    private static String getFormatForSUMIF(int startPos, int endPosOfProjects, int valueToCheckPos) {
        return String.format("=SUMIF($B$%d:$B$%d,B%d,$A$%d:$A$%d)",
                startPos, endPosOfProjects, valueToCheckPos,
                startPos, endPosOfProjects);
    }

    private static List<ProjectRowReference> rowsForRerferencesPerProject = new ArrayList<>();
    private static int writeProjectsToSpreadsheet(String worksheetName, FileSettings settings, TrackedTimeSummary trackedTimeSum, int startPos) {
        for (String projectName : trackedTimeSum.getProjectNames()) {
            ProjectSummarySpreadsheetUpdater export = new ProjectSummarySpreadsheetUpdater(settings.exportFileId,
                    trackedTimeSum.receiveTrackedTimesPerProject(projectName));
            export.WriteProjectToSpreadSheet(startPos, worksheetName);
            rowsForRerferencesPerProject.add(export.getRowInformationForReferences());
            listOfSums.add(new Integer(export.getLastPosition()-2));
            startPos = export.getLastPosition() + 1;
        }
        return startPos;
    }

    private static int posForSumUp = 0;
    private static int writeSummaryAtTheEndOfTheList(String worksheetName, FileSettings settings,
                                                      int row, List<Integer> projectSumRows, int startSearchPos, int endSearchPos) {
        Spreadsheet exportSpreadsheet = new Spreadsheet(settings.exportFileId);
        exportSpreadsheet.insertValueIntoCell(worksheetName,1, row, "GESAMTUMSATZ");
        exportSpreadsheet.insertValueIntoCell(worksheetName,4, row, String.format("=SUM(D1:D%d)/2", row-1));
        exportSpreadsheet.insertValueIntoCell(worksheetName,5, row, String.format("=SUM(E1:E%d)/2", row-1));
        exportSpreadsheet.insertValueIntoCell(worksheetName,6, row, String.format("=SUM(F1:F%d)/2", row-1));
        //
        exportSpreadsheet.insertValueIntoCell(worksheetName,3, row+1,
                String.format("=if(D%d<>D%d,\"da stimmt was nicht\",\"Summen passen\")", row, ++row));

        posForSumUp = row;
        row++;

        exportSpreadsheet.insertValueIntoCell(worksheetName,2, row, "Test User");
        exportSpreadsheet.insertValueIntoCell(worksheetName,3, row, "Eigene Stunden");
        exportSpreadsheet.insertValueIntoCell(worksheetName,4, row,
                getFormatForSUMIF(startSearchPos, endSearchPos, row));
        exportSpreadsheet.insertValueIntoCell(worksheetName,5, row, "50");
        exportSpreadsheet.insertValueIntoCell(worksheetName,7, row, String.format("=D%d*E%d", row, row++));

        exportSpreadsheet.insertValueIntoCell(worksheetName,5, row++, "gerundet");

        exportSpreadsheet.insertValueIntoCell(worksheetName,3, row, "Teamstunden");
        exportSpreadsheet.insertValueIntoCell(worksheetName,4, row,
                String.format("=A%d-D%d", settings.getHourRateAsHashMapPerTeamMember().size()+1, row-2));
        exportSpreadsheet.insertValueIntoCell(worksheetName,5, row,
                String.format("=IF(MOD(D%d,50)=0,D%d,(ROUNDDOWN(D%d/50,0)+1)*50)",
                        row, row, row));
        exportSpreadsheet.insertValueIntoCell(worksheetName,6, row, "4.00");
        exportSpreadsheet.insertValueIntoCell(worksheetName,7, row,
                String.format("=MIN(E%d*F%d,G%d)", row, row, row-2));
        row++;

        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, row, "Summe für Rechnung");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 7, row,
                String.format("=SUM(G%d:G%d)", row-3, row-1));
        row++;

        exportSpreadsheet.insertValueIntoCell(worksheetName, 3, row, "effektiver Stundensatz");
        exportSpreadsheet.insertValueIntoCell(worksheetName, 7, row,
                String.format("=G%d/D%d", row-1, row-4));

        return row+3;
    }



    /*
    private static void TestMethodsForSpreadSheets() {
        Spreadsheet mySpreadsheet = new Spreadsheet("1MBc1Uvv4Wfyw31mwoGnrEzfCaXxcd1BT-aLg0x1VS_Y");
        System.out.println(mySpreadsheet.toString());
        mySpreadsheet.addNewWorksheet("MartinsSpreadSheet");
        mySpreadsheet.copyWorksheet("Tabellenblatt1", "CopiedWorksheet");
        mySpreadsheet.insertValueIntoCell("Tabellenblatt2", 4, 15, "My name is Martin");
        String value = mySpreadsheet.receiveValueAtKey("Tabellenblatt2", "E11");

        int valueAsInteger ;
        try {
            valueAsInteger = Integer.parseInt(value);
            valueAsInteger++;
            value = Integer.toString(valueAsInteger);
        } catch (NumberFormatException e) {
            value = "Could not convert item";
        }
        mySpreadsheet.insertValueIntoCell("Tabellenblatt2", 5, 11, value);

        mySpreadsheet.update();
        System.out.println(mySpreadsheet.toString());
        mySpreadsheet.deleteWorksheet("MartinsSpreadSheet");
        mySpreadsheet.deleteWorksheet("CopiedWorksheet");
    }
*/
}