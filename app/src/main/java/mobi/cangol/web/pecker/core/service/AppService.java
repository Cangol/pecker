package mobi.cangol.web.pecker.core.service;


import mobi.cangol.web.pecker.core.model.App;
import mobi.cangol.web.pecker.core.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppService extends AbstractService<App> {
    @Autowired
    private AppRepository appRepository;

    protected JpaRepository<App, Long> getRepository() {
        return appRepository;
    }

    public List<App> findByAgent(String agent) {
        App t = new App();
        t.setAgent(agent);
        return getRepository().findAll(Example.of(t));
    }

}
