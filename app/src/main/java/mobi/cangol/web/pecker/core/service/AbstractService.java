package mobi.cangol.web.pecker.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class AbstractService<T> {

    private static final int PAGE_SIZE = 20;

    protected abstract JpaRepository<T, Long> getRepository();

    @Transactional
    public Page<T> getList(Integer pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "id");
        return getRepository().findAll(pageRequest);
    }

    @Transactional
    public List<T> getAllList() {
        return getRepository().findAll();
    }

    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public T get(Long id) {
        return getRepository().findById(id).orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        getRepository().deleteById(id);
    }

    @Transactional
    public void delete(T t) {
        getRepository().delete(t);
    }
}