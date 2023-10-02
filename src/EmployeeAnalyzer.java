import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmployeeAnalyzer {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\Nikhil\\Downloads\\Assignment_Timecard.xlsx - Sheet1.csv";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine(); 

            
            String positionIDColumnName = "Position ID";
            String timeInColumnName = "Time";
            String timeOutColumnName = "Time Out";
            String nameColumnName = "Employee Name";
            String hoursColumnName = "Timecard Hours (as Time)";

            
            int positionIDIndex = -1;
            int timeInIndex = -1;
            int timeOutIndex = -1;
            int nameIndex = -1;
            int hoursIndex = -1;

            String[] headerColumns = header.split(cvsSplitBy);

            for (int i = 0; i < headerColumns.length; i++) {
                if (headerColumns[i].equalsIgnoreCase(positionIDColumnName)) {
                    positionIDIndex = i;
                } else if (headerColumns[i].equalsIgnoreCase(timeInColumnName)) {
                    timeInIndex = i;
                } else if (headerColumns[i].equalsIgnoreCase(timeOutColumnName)) {
                    timeOutIndex = i;
                } else if (headerColumns[i].equalsIgnoreCase(nameColumnName)) {
                    nameIndex = i;
                } else if (headerColumns[i].equalsIgnoreCase(hoursColumnName)) {
                    hoursIndex = i;
                }
            }

            if (positionIDIndex == -1 || timeInIndex == -1 || timeOutIndex == -1 || nameIndex == -1 || hoursIndex == -1) {
                System.err.println("Required columns not found in the CSV file.");
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");

            String currentEmployee = null;
            int consecutiveDays = 0;
            Date lastTimeOut = null;

            while ((line = br.readLine()) != null) {
                String[] record = line.split(cvsSplitBy);

                String employeeName = record[nameIndex];
                String positionID = record[positionIDIndex];

                try {
                    Date timeIn = parseDate(record[timeInIndex], dateFormat);
                    Date timeOut = parseDate(record[timeOutIndex], dateFormat);
                    double hoursWorked = parseHours(record[hoursIndex]);

                    if (currentEmployee == null || !currentEmployee.equals(employeeName)) {
                        consecutiveDays = 0;
                        currentEmployee = employeeName;
                    }

                    if (lastTimeOut != null) {
                        long timeDiffMillis = timeIn.getTime() - lastTimeOut.getTime();
                        double hoursBetweenShifts = timeDiffMillis / (60 * 60 * 1000);

                        if (hoursBetweenShifts > 1 && hoursBetweenShifts < 10) {
                            consecutiveDays++;
                        } else {
                            consecutiveDays = 0;
                        }
                    }

                    if (consecutiveDays == 7) {
                        System.out.println("Employee " + employeeName + " (Position: " + positionID + ") worked for 7 consecutive days.");
                    }

                    if (hoursWorked > 14) {
                        System.out.println("Employee " + employeeName + " (Position: " + positionID + ") worked for more than 14 hours in a single shift.");
                    }

                    lastTimeOut = timeOut;
                } catch (ParseException e) {
                    
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Date parseDate(String dateString, SimpleDateFormat dateFormat) throws ParseException {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            
            SimpleDateFormat altDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            return altDateFormat.parse(dateString);
        }
    }

    private static double parseHours(String hoursString) {
        String[] parts = hoursString.split(":");
        if (parts.length == 2) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours + (minutes / 60.0); 
        } else {
            return 0.0; 
        }
    }
}
