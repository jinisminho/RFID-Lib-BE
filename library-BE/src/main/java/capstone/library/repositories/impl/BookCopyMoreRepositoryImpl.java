package capstone.library.repositories.impl;

import capstone.library.entities.Book;
import capstone.library.entities.BookCopy;
import capstone.library.repositories.BookCopyMoreRepository;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
@Transactional
public class BookCopyMoreRepositoryImpl implements BookCopyMoreRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @SuppressWarnings("unchecked")
    public Page<BookCopy> findBookCopies(String searchValue, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<BookCopy> result1, result2;

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int offset = page * size;
        int limit = size;

        long totalSize = 0;
        totalSize = searchSession.search(BookCopy.class)
                .where(f -> f.match()
                        .fields("book.title", "book.title_2", "book.sub", "book.sub_2")
                        .matching(searchValue)
                        .analyzer("default")
                ).fetchTotalHitCount();
        result1 = searchSession.search(BookCopy.class)
                .where(f -> f.match()
                        .fields("book.title", "book.title_2", "book.sub", "book.sub_2")
                        .matching(searchValue)
                        .analyzer("default")
                )
                .fetch(offset, limit);

        result2 = searchSession.search(BookCopy.class)
                .where(f -> f.match()
                        .fields("book.isbn", "barcode", "rfid")
                        .matching(searchValue)
                        .analyzer("keyword")
                )
                .fetch(offset, limit);

        List<BookCopy> res = new ArrayList<>();
        if (result1.total().hitCount() > 0) res.addAll(result1.hits());
        if (result2.total().hitCount() > 0) {
            totalSize = searchSession.search(Book.class)
                    .where(f -> f.match()
                            .fields("isbn")
                            .matching(searchValue)
                            .analyzer("keyword")
                    ).fetchTotalHitCount();
            res = result2.hits();
        }

        List<BookCopy> resWithoutDuplicates = new ArrayList<>(
                new LinkedHashSet<>(res));
        return new PageImpl<BookCopy>(resWithoutDuplicates, pageable, totalSize);
    }

    @Override
    public void reindexAll() {
        SearchSession searchSession = Search.session(entityManager);

        // Reindex all
        try {
            searchSession.massIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
    }

}
