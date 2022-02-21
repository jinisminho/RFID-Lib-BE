package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.response.WishlistResDto;
import capstone.library.services.WishlistService;
import capstone.library.util.ApiPageable;
import capstone.library.util.constants.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @ApiOperation(value = "This API create new wishlist")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/addWishlist")
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public ResponseEntity<?> addWishlist(@RequestParam(required = true, value = "bookID") Integer bookId,
                                         @RequestParam(required = true, value = "patronID") Integer patronId) {

        boolean bool = wishlistService.addWishlist(bookId, patronId);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to Added new wishlist");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "This API get the wishlist by patronId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @ApiPageable
    @GetMapping("/getWishlist")
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public Page<WishlistResDto> getWishlist(@RequestParam(required = true, value = "patronID") Integer patronId, @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {

        return wishlistService.getWishlist(patronId, pageable);

    }

}
