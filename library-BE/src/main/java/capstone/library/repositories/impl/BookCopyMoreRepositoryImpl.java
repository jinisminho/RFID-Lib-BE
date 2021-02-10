package capstone.library.repositories.impl;

import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import capstone.library.repositories.BookCopyMoreRepository;
import org.hibernate.search.engine.search.query.SearchQuery;
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
        SearchQuery<BookCopy> query = searchSession.search(BookCopy.class)
                .where(f -> f.match()
                        .fields("book.title", "book.title_2", "book.sub", "book.sub_2")
                        .matching(searchValue)
                        .analyzer("default")
                )
                .toQuery();
        totalSize = query.fetchTotalHitCount();
        result1 = query.fetch(offset, limit);

        SearchQuery<BookCopy> query2 = searchSession.search(BookCopy.class)
                .where(f -> f.match()
                        .fields("book.isbn", "barcode", "rfid")
                        .matching(searchValue)
                        .analyzer("keyword")
                )
                .toQuery();
        result2 = query2.fetch(offset, limit);

        List<BookCopy> res = new ArrayList<>();
        if (result1.total().hitCount() > 0) res.addAll(result1.hits());
        if (result2.total().hitCount() > 0) {
            totalSize = query2.fetchTotalHitCount();
            res = result2.hits();
        }

        List<BookCopy> resWithoutDuplicates = new ArrayList<>(
                new LinkedHashSet<>(res));
        return new PageImpl<BookCopy>(resWithoutDuplicates, pageable, totalSize);
    }

    @Override
    public Page<BookCopy> findBookCopiesWithStatus(String searchValue, List<BookCopyStatus> status, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int offset = page * size;
        int limit = size;

        int totalSize;
        List<BookCopy> res = new ArrayList<>();

        List<SearchQuery> queries = new ArrayList<>();

        status.forEach(s -> {
                    SearchQuery<BookCopy> query = searchSession.search(BookCopy.class)
                            .where(f -> f.bool()
                                    .must(f.match()
                                            .fields("book.isbn", "barcode", "rfid")
                                            .matching(searchValue)
                                            .analyzer("keyword"))
                                    .must(f.match()
                                            .fields("status")
                                            .matching(s)
                                            .analyzer("keyword"))
                            )
                            .toQuery();
                    queries.add(query);
                }
        );
        totalSize = queries.stream().mapToInt(searchQuery -> (int) searchQuery.fetchTotalHitCount()).sum();
        queries.forEach(searchQuery -> {
            res.addAll(searchQuery.fetch(offset, limit).hits());
        });
        if (totalSize > 0) return new PageImpl<BookCopy>(new ArrayList<>(
                new LinkedHashSet<>(res)), pageable, totalSize);

        queries.clear();
        res.clear();
        status.forEach(s -> {
                    SearchQuery<BookCopy> query = searchSession.search(BookCopy.class)
                            .where(f -> f.bool()
                                    .must(f.match()
                                            .fields("book.title", "book.title_2", "book.sub", "book.sub_2")
                                            .matching(searchValue)
                                            .analyzer("default"))
                                    .must(f.match()
                                            .fields("status")
                                            .matching(s)
                                            .analyzer("keyword"))
                            )
                            .toQuery();
                    queries.add(query);
                }
        );
        totalSize = queries.stream().mapToInt(searchQuery -> (int) searchQuery.fetchTotalHitCount()).sum();
        queries.forEach(searchQuery -> {
            res.addAll(searchQuery.fetch(offset, limit).hits());
        });

        return new PageImpl<BookCopy>(new ArrayList<>(
                new LinkedHashSet<>(res)), pageable, totalSize);
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
