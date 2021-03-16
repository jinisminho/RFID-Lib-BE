package capstone.library.util.tools;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CallNumberUtil {

    /*Use to insert or update book
     * FE input DDC, Author, Publish Year then send request
     * BE create call number from the above and insert/update db*/
    public String createCallNumber(double ddc, String author, int publishYear) {
        /*Call number example: (DDC AUTHOR YEAR)
         *   100 ROW 2010
         *   750.12 NIS 2008*/

        //Author code is the first 3 characters of the author's last name
        String[] authorNameSplit = author.split(" ");
        String authorLastName = authorNameSplit[authorNameSplit.length - 1];
        String authorCode;
        if (authorLastName.length() > 3) {
            authorCode = authorNameSplit[authorNameSplit.length - 1].substring(0, 3).toUpperCase();
        } else {
            authorCode = authorLastName.toUpperCase();
        }
        return convertFloatToString(ddc) + " " + authorCode + " " + publishYear;
    }

    //Reformat double
    private String convertFloatToString(double ddc) {
        if (ddc == (long) ddc) {
            return String.format("%03d", (long) ddc);
        } else {
            return String.format("%s", ddc);
        }
    }
}
