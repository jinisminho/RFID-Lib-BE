package capstone.library.util.tools;

import capstone.library.entities.Genre;
import capstone.library.exceptions.MissingInputException;

import java.util.List;

public class GenreUtil {

    public static String getGenreFormCallNumber(String callNumber, List<Genre> genres){
        if(callNumber == null){
            throw new MissingInputException("missing callNumber");
        }
        if(callNumber.length() < 3){
            throw new IllegalArgumentException("callNumber is invalid");
        }
        try {
            int callNumberPrefix = Integer.parseInt(callNumber.substring(0, 3));
            if (!genres.isEmpty()) {
                Genre holdingGenre = genres.get(0);
                for (Genre genre : genres) {
                    int tmpDDC = Integer.parseInt(genre.getDdc().toString());
                    if (tmpDDC > callNumberPrefix) {
                        break;
                    }else{
                        holdingGenre = genre;
                    }
                }
                return holdingGenre.getName();
            } else {
                return "N/A";
            }
        }catch (NumberFormatException e){
            throw new RuntimeException("Cannot format call number prefix");
        }
    }
}
