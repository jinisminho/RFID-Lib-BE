package capstone.library.util.tools;

import capstone.library.dtos.others.ValidateExcelObject;
import capstone.library.entities.Account;
import capstone.library.entities.Profile;
import capstone.library.enums.Gender;
import capstone.library.exceptions.ImportFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelUtil {

    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String[] HEADERs = {"Email", "RFID", "Full Name", "Phone", "Gender", "Avatar"};
    private static final String SHEET = "Patrons";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+(\\.[A-Za-z]{2,4}){1,2}$";
    private static final String PHONE_PATTERN = "^(0)[0-9]{9}$";
    private static final String GENDER_PATTERN = "^F|M$";
    private static final String FULL_NAME_PATTERN = "^[\\p{L} ]{1,50}$";


    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static ValidateExcelObject excelToAccounts(InputStream is) {
        StringBuilder msg = new StringBuilder();
        List<Account> accounts = new ArrayList<Account>();
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet != null) {
                Iterator<Row> rows = sheet.iterator();
                int rowNumber = 0;
                //get each row
                while (rows.hasNext()) {

                    Row currentRow = rows.next();
                    // skip header
                    if (rowNumber == 0) {
                        //check header
                        if (!checkHeader(currentRow)) {
                            throw new ImportFileException("Header has wrong format, should be: "
                                    + Arrays.stream(HEADERs).sequential().collect(Collectors.joining(",")));
                        }
                        rowNumber++;
                        continue;
                    }

                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    Account account = new Account();
                    Profile profile = new Profile();
                    profile.setAccount(account);
                    account.setProfile(profile);
                    String email;
                    String phone;
                    String rfid;
                    String fullName;
                    String gender;
                    String avatar;
                    int cellIdx = 0;
                    StringBuilder rowMsg = new StringBuilder();
                    //get each cell in 1 row
                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();
                        switch (cellIdx) {
                            case 0:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    email = currentCell.getStringCellValue().trim();
                                    System.out.println(email);
                                    if (email.isEmpty() || email.length() > 100 || !email.matches(EMAIL_PATTERN)) {
                                        rowMsg.append(" Email is invalid;");
                                    } else {
                                        account.setEmail(email);
                                    }
                                } else {
                                    rowMsg.append(" Email must be text");
                                }
                                break;

                            case 1:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    rfid = currentCell.getStringCellValue().trim();
                                    if (rfid.isEmpty() || rfid.length() > 80) {
                                        rowMsg.append(" Rfid is invalid: required, max: 80;");
                                    } else {
                                        account.setRfid(currentCell.getStringCellValue());
                                    }
                                } else {
                                    rowMsg.append(" Rfid must be text");
                                }
                                break;

                            case 2:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    fullName = currentCell.getStringCellValue().trim();
                                    if (fullName.isEmpty() || !fullName.matches(FULL_NAME_PATTERN)) {
                                        rowMsg.append(" Full name is invalid: required, max: 50;");
                                    } else {
                                        account.getProfile().setFullName(currentCell.getStringCellValue());
                                    }
                                } else {
                                    rowMsg.append(" Full name must be text");
                                }
                                break;

                            case 3:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING || currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        phone = DoubleFormatter.formatToDecimal(currentCell.getNumericCellValue());
                                    } else {
                                        phone = currentCell.getStringCellValue().trim();
                                    }
                                    if (!phone.matches(PHONE_PATTERN)) {
                                        rowMsg.append(" Phone is invalid: required, 10 digits;");
                                    } else {
                                        account.getProfile().setPhone(currentCell.getStringCellValue());
                                    }
                                } else {
                                    rowMsg.append(" Phone must be text or number");
                                }
                                break;

                            case 4:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    gender = currentCell.getStringCellValue().trim();
                                    if (!gender.matches(GENDER_PATTERN)) {
                                        rowMsg.append(" Gender is invalid: F or M;");
                                    } else {
                                        account.getProfile().setGender(gender.equals("F") ? Gender.F : Gender.M);
                                    }
                                } else {
                                    rowMsg.append(" Gender must be text");
                                }

                                break;

                            case 5:
                                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    avatar = currentCell.getStringCellValue().trim();
                                    if (avatar.isEmpty() || avatar.length() > 500) {
                                        rowMsg.append(" Avatar is invalid: required, max: 500;");
                                    } else {
                                        account.setAvatar(currentCell.getStringCellValue());
                                    }
                                } else {
                                    rowMsg.append(" Gender must be text");
                                }
                                break;
                            default:
                                rowMsg.append(" Total column must be 6;");
                        }

                        cellIdx++;

                    }
                    accounts.add(account);
                    if (!rowMsg.toString().isEmpty()) {
                        String tmp = rowMsg.toString();
                        if (tmp.endsWith(";")) {
                            tmp = tmp.substring(0, tmp.length() - 1);
                        }
                        msg.append(" Row ").append((currentRow.getRowNum() + 1)).append(": ").append(tmp).append(" -- ");
                    }
                }
            } else {
                msg.append("Cannot find any sheet");
            }
            if(msg.toString().isEmpty()){
                return new ValidateExcelObject(true, "", accounts);
            }else {
                String tmp = msg.toString().trim();
               // System.out.println("test" + tmp.substring( tmp.length() - 2, tmp.length()));

                if(tmp.substring( tmp.length() - 2, tmp.length()).equals("--")){
                    tmp = tmp.substring(0, tmp.length() - 3);
                }
                return new ValidateExcelObject(false, tmp, accounts);
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }

    }

    private static boolean checkHeader(Row header) {
        Iterator<Cell> cellsInRow = header.iterator();
        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            switch (cellIdx) {
                case 0:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[0])) {
                        return false;
                    }
                    break;
                case 1:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[1])) {
                        return false;
                    }
                    break;
                case 2:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[2])) {
                        return false;
                    }
                    break;
                case 3:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[3])) {
                        return false;
                    }
                    break;
                case 4:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[4])) {
                        return false;
                    }
                    break;
                case 5:
                    if (!currentCell.getStringCellValue().trim().equals(HEADERs[5])) {
                        return false;
                    }
                    break;
                default:
                    throw new ImportFileException("Number of column must be 6");

            }
            cellIdx++;

        }
        return true;
    }

}
