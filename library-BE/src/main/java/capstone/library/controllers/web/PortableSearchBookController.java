package capstone.library.controllers.web;

import capstone.library.dtos.request.AddPortableSearchingBooksRequest;
import capstone.library.services.PortableSearchBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/portableSearch")
public class PortableSearchBookController {

    @Autowired
    private PortableSearchBookService portableSearchBookService;

    @PostMapping("/add")
    public String addBookToSearchingCart (@RequestBody @Valid AddPortableSearchingBooksRequest request){
        return portableSearchBookService.addSearchingBooksToFile(request);
    }

    @GetMapping("/delete/{accountId}")
    public String deleteSearchingCart(@PathVariable(name = "accountId")int accountId){
        return portableSearchBookService.deleteSearchingFileOfAPatron(accountId);
    }
}
